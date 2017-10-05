package com.ruflux.redpaper.data.local;

import android.content.Context;
import android.database.Cursor;

import com.ruflux.redpaper.data.local.model.PostDb;
import com.ruflux.redpaper.data.local.model.PostModel;
import com.ruflux.redpaper.data.local.model.SubModel;
import com.ruflux.redpaper.data.model.Post;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LocalSource {

    private BriteDatabase db;

    public LocalSource(Context context) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        db = sqlBrite.wrapDatabaseHelper(new DbHelper(context), Schedulers.io());
    }

    public Observable<List<Post>> getPostsFrom(String sub) {
        SqlDelightStatement statement = PostDb.FACTORY.SelectAllPostsBySub(sub);
        return db.createQuery(statement.tables, statement.statement, statement.args)
                .mapToList(new Function<Cursor, Post>() {
                    @Override
                    public Post apply(@NonNull Cursor cursor) throws Exception {
                        Post p = new Post();
                        p.setId(PostDb.MAPPER.map(cursor).post_id());
                        p.setDomain(PostDb.MAPPER.map(cursor).domain());
                        p.setTitle(PostDb.MAPPER.map(cursor).title());
                        p.setUrl(PostDb.MAPPER.map(cursor).url());
                        p.setIsSelf(PostDb.MAPPER.map(cursor).is_self());
                        p.setFilename(PostDb.MAPPER.map(cursor).filename());
                        p.setThumbnailUrl(PostDb.MAPPER.map(cursor).thumbnail_url());
                        p.setHeight(PostDb.MAPPER.map(cursor).height());
                        p.setWidth(PostDb.MAPPER.map(cursor).width());

                        return p;
                    }
                });
    }

    public void savePostsSingleTransaction(String sub, List<Post> posts) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            PostModel.DeletePostBySub deleteStatement = new PostModel.DeletePostBySub(db.getWritableDatabase());
            PostModel.InsertOrReplacePost insertPostStatement = new PostModel.InsertOrReplacePost(db.getWritableDatabase());
            SubModel.InsertOrReplaceSub insertSubStatement = new SubModel.InsertOrReplaceSub(db.getWritableDatabase());

            db.executeUpdateDelete(deleteStatement.table, deleteStatement.program);
            insertSubStatement.bind(sub);
            db.executeInsert(insertSubStatement.table, insertSubStatement.program);
            for (Post post : posts) {
                insertPostStatement.bind(post.getId(), post.getDomain(), post.getTitle(),
                        post.getUrl(), post.getIsSelf(), post.getFilename(),
                        post.getThumbnailUrl(), post.getHeight(), post.getWidth(), sub);
                db.executeInsert(insertPostStatement.table, insertPostStatement.program);
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}

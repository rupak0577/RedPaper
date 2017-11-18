package com.ruflux.redpaper.data.local;

import android.database.Cursor;

import com.ruflux.redpaper.data.local.model.PostDb;
import com.ruflux.redpaper.data.local.model.PostModel;
import com.ruflux.redpaper.data.local.model.ResolutionDb;
import com.ruflux.redpaper.data.local.model.ResolutionModel;
import com.ruflux.redpaper.data.local.model.SourceDb;
import com.ruflux.redpaper.data.local.model.SourceModel;
import com.ruflux.redpaper.data.local.model.SubModel;
import com.ruflux.redpaper.data.model.Post;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class LocalSource {

    private BriteDatabase db;

    public LocalSource(BriteDatabase briteDatabase) {
        db = briteDatabase;
    }

    public Observable<List<Post>> getPostsFrom(String sub) {
        SqlDelightStatement statement = PostDb.FACTORY.SelectAllPostsBySub(sub);
        return db.createQuery(statement.tables, statement.statement, statement.args)
                .mapToList(cursor -> {
                    Post post = new Post();
                    post.setDomain(PostDb.MAPPER.map(cursor).domain());
                    post.setId(PostDb.MAPPER.map(cursor).post_id());
                    post.setTitle(PostDb.MAPPER.map(cursor).title());
                    post.setUrl(PostDb.MAPPER.map(cursor).url());

                    // Read from Source table
                    SqlDelightStatement sourceStatement = SourceDb.FACTORY
                            .SelectAllSourceByPost(post.getId());
                    Cursor sourceCursor = db.getReadableDatabase().rawQuery(sourceStatement.statement, sourceStatement.args);
                    sourceCursor.moveToFirst();

                    Post.Preview.Source source = new Post.Preview.Source();
                    source.setUrl(SourceDb.MAPPER.map(sourceCursor).url());
                    source.setWidth(SourceDb.MAPPER.map(sourceCursor).width());
                    source.setHeight(SourceDb.MAPPER.map(sourceCursor).height());

                    sourceCursor.close();

                    // Read from Resolutions table
                    SqlDelightStatement resStatement = ResolutionDb.FACTORY
                            .SelectAllResolutionByPost(post.getId());
                    Cursor resCursor = db.getReadableDatabase().rawQuery(resStatement.statement, resStatement.args);

                    List<Post.Preview.Resolution> resolutions = new ArrayList<>();
                    while (resCursor.moveToNext()) {
                        Post.Preview.Resolution resolution = new Post.Preview.Resolution();
                        resolution.setUrl(ResolutionDb.MAPPER.map(resCursor).url());
                        resolution.setWidth(ResolutionDb.MAPPER.map(resCursor).width());
                        resolution.setHeight(ResolutionDb.MAPPER.map(resCursor).height());

                        resolutions.add(resolution);
                    }

                    resCursor.close();

                    Post.Preview.Image image = new Post.Preview.Image();
                    image.setSource(source);
                    image.setResolutions(resolutions);

                    List<Post.Preview.Image> images = new ArrayList<>();
                    images.add(image);

                    Post.Preview preview = new Post.Preview();
                    preview.setImages(images);
                    post.setPreview(preview);

                    return post;
                });
    }

    public boolean isSubEmpty(String sub) {
        SqlDelightStatement statement = PostDb.FACTORY.CountAllPosts(sub);
        Cursor cursor = db.getReadableDatabase().rawQuery(statement.statement, statement.args);

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return (count == 0);
    }

    public void savePostsSingleTransaction(String sub, List<Post> posts) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            PostModel.DeletePostBySub deleteStatement = new PostModel.DeletePostBySub(db.getWritableDatabase());
            PostModel.InsertPost insertPostStatement = new PostModel.InsertPost(db.getWritableDatabase());
            SourceModel.InsertSource insertSourceStatment = new SourceModel.InsertSource(db.getWritableDatabase());
            ResolutionModel.InsertResolution insertResolutionStatement = new ResolutionModel.InsertResolution(db.getWritableDatabase());
            SubModel.InsertOrReplaceSub insertSubStatement = new SubModel.InsertOrReplaceSub(db.getWritableDatabase());

            db.executeUpdateDelete(deleteStatement.table, deleteStatement.program);
            insertSubStatement.bind(sub);
            db.executeInsert(insertSubStatement.table, insertSubStatement.program);
            for (Post post : posts) {
                insertPostStatement.bind(post.getId(), post.getDomain(), post.getTitle(),
                        post.getUrl(), sub);
                db.executeInsert(insertPostStatement.table, insertPostStatement.program);

                insertSourceStatment.bind(post.getId(), post.getPreview().getImages().get(0).getSource().getUrl(),
                        post.getPreview().getImages().get(0).getSource().getWidth(),
                        post.getPreview().getImages().get(0).getSource().getHeight());
                db.executeInsert(insertSourceStatment.table, insertSourceStatment.program);

                for (Post.Preview.Resolution resolution : post.getPreview().getImages().get(0).getResolutions()) {
                    insertResolutionStatement.bind(post.getId(), resolution.getUrl(), resolution.getWidth(),
                            resolution.getHeight());
                    db.executeInsert(insertResolutionStatement.table, insertResolutionStatement.program);
                }
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}

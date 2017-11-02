package com.ruflux.redpaper.data.local;

import android.content.Context;

import com.ruflux.redpaper.data.local.model.PostDb;
import com.ruflux.redpaper.data.local.model.PostModel;
import com.ruflux.redpaper.data.local.model.ResolutionDb;
import com.ruflux.redpaper.data.local.model.ResolutionModel;
import com.ruflux.redpaper.data.local.model.SourceDb;
import com.ruflux.redpaper.data.local.model.SourceModel;
import com.ruflux.redpaper.data.local.model.SubModel;
import com.ruflux.redpaper.data.model.Post;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class LocalSource {

    private BriteDatabase db;

    public LocalSource(Context context) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        db = sqlBrite.wrapDatabaseHelper(new DbHelper(context), Schedulers.io());
    }

    public Single<List<Post>> getPostsFrom(String sub) {
        SqlDelightStatement statement = PostDb.FACTORY.SelectAllPostsBySub(sub);
        return Single.fromObservable(db.createQuery(statement.tables, statement.statement, statement.args)
                .mapToList(cursor -> {
                    Post post = new Post();
                    post.setDomain(PostDb.MAPPER.map(cursor).domain());
                    post.setId(PostDb.MAPPER.map(cursor).post_id());
                    post.setTitle(PostDb.MAPPER.map(cursor).title());
                    post.setUrl(PostDb.MAPPER.map(cursor).url());
                    post.setIsSelf(PostDb.MAPPER.map(cursor).is_self());

                    Post.Preview.Image image = new Post.Preview.Image();
                    getSource(PostDb.MAPPER.map(cursor).post_id())
                            .subscribe(image::setSource);
                    getResolutions(PostDb.MAPPER.map(cursor).post_id())
                            .subscribe(image::setResolutions);

                    List<Post.Preview.Image> images = new ArrayList<>();
                    images.add(image);

                    Post.Preview preview = new Post.Preview();
                    preview.setImages(images);
                    post.setPreview(preview);

                    return post;
                }));
    }

    private Single<Post.Preview.Source> getSource(String post_id) {
        SqlDelightStatement statement = SourceDb.FACTORY.SelectAllSourceByPost(post_id);
        return Single.fromObservable(db.createQuery(statement.tables, statement.statement, statement.args)
                .mapToOne(cursor -> {
                    Post.Preview.Source source = new Post.Preview.Source();
                    source.setUrl(SourceDb.MAPPER.map(cursor).url());
                    source.setWidth(SourceDb.MAPPER.map(cursor).width());
                    source.setHeight(SourceDb.MAPPER.map(cursor).height());

                    return source;
                }));
    }

    private Single<List<Post.Preview.Resolution>> getResolutions(String post_id) {
        SqlDelightStatement statement = ResolutionDb.FACTORY.SelectAllResolutionByPost(post_id);
        return Single.fromObservable(db.createQuery(statement.tables, statement.statement, statement.args)
                .mapToList(cursor -> {
                    Post.Preview.Resolution resolution = new Post.Preview.Resolution();
                    resolution.setUrl(ResolutionDb.MAPPER.map(cursor).url());
                    resolution.setWidth(ResolutionDb.MAPPER.map(cursor).width());
                    resolution.setHeight(ResolutionDb.MAPPER.map(cursor).height());

                    return resolution;
                }));
    }

    public boolean isSubEmpty(String sub) {
        SqlDelightStatement statement = PostDb.FACTORY.SelectAllPostsBySub(sub);
        return (db.getReadableDatabase().rawQuery(statement.statement, statement.args)
                .getCount() == 0);
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
                        post.getUrl(), post.getIsSelf(), sub);
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

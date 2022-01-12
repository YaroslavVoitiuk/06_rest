package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImpl implements PostRepository{

    private Map<Long, Post> posts = new ConcurrentHashMap<>();
    AtomicLong id = new AtomicLong();

    public PostRepositoryImpl(){
        posts.put(1L,new Post(1,"First Post"));
        posts.put(2L,new Post(2,"Second Post"));
        id.set(posts.size());
    }

    public List<Post> all() {
        List<Post> postsList = new ArrayList<>();
        for (Map.Entry<Long, Post> entry : posts.entrySet()) {
            if(!entry.getValue().isRemoved())
                postsList.add(entry.getValue());
        }
        return postsList;
    }

    public Optional<Post> getById(long id) {
        if(posts.get(id).isRemoved()) throw new NotFoundException();
        return Optional.ofNullable(posts.get(id));

    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            Post newPost = new Post(id.incrementAndGet(), post.getContent());
            posts.put(id.get(), newPost);
        } else if (post.getId() != 0 && posts.containsKey(post.getId()) && !posts.get(post.getId()).isRemoved()) {
            posts.put(post.getId(), post);
        } else {
            throw new NotFoundException("no such posts found");
        }
        return post;
    }

    public void removeById(long postId) {
        if(posts.containsKey(postId) && !posts.get(postId).isRemoved()){
            posts.get(postId).setRemoved(true);
        }else throw new NotFoundException("no such posts found");
    }
}

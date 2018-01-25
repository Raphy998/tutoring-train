/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.EntryType;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.exceptions.CommentNotFoundException;
import edu.tutoringtrain.data.exceptions.EntryNotFoundException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Comment;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class CommentService extends AbstractService {
    @Inject
    UserService userService;
    @Inject
    EntryService entryService;
    
    public CommentService() {
    }
    
    @Transactional
    public List<Comment> getComments(EntryType type, Integer entryID) throws NullValueException {
        if (entryID == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        if (em.find(Entry.class, new BigDecimal(entryID)) == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NOT_FOUND).withParams(entryID));
        }
        
        TypedQuery<Comment> query = (TypedQuery<Comment>) em.createNamedQuery("Comment.findByEntryId");
        query.setParameter("entryId", entryID);
        
        return query.getResultList();
    }
    
    @Transactional
    public Comment createComment(EntryType type, Integer entryID, Comment comment) throws NullValueException, EntryNotFoundException, UserNotFoundException {
        if (entryID == null || comment == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        Entry e = entryService.getEntry(type, new BigDecimal(entryID));
        
        Comment newComment = new Comment();
        newComment.setEntry(e);
        newComment.setUser(userService.getUserByUsername(comment.getUser().getUsername()));
        newComment.setText(comment.getText());
        newComment.setPostedon(DateUtils.toDate(LocalDateTime.now()));
        
        em.persist(newComment);

        return newComment;
    }
    
    @Transactional
    public void updateComment(EntryType type, Integer entryID, Comment comment) throws NullValueException, EntryNotFoundException, UserNotFoundException, CommentNotFoundException {
        if (entryID == null || comment == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        Comment persistedComment = em.find(Comment.class, comment.getId());
        if (persistedComment == null || !persistedComment.getEntry().getId().equals(new BigDecimal(entryID))) {
            throw new CommentNotFoundException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.COMMENT_NOT_FOUND).withParams(comment.getId(), type.toString(), entryID));
        }
        else {
            persistedComment.setText(comment.getText());
        }
    }
    
    @Transactional
    public void updateComment(EntryType type, Integer entryID, Comment comment, String username) throws NullValueException, EntryNotFoundException, UserNotFoundException, CommentNotFoundException {
        if (entryID == null || comment == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        Comment persistedComment = em.find(Comment.class, comment.getId());
        if (persistedComment == null || !persistedComment.getEntry().getId().equals(new BigDecimal(entryID)) || 
                !persistedComment.getUser().getUsername().equals(username)) {
            throw new CommentNotFoundException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.COMMENT_OF_USER_NOT_FOUND)
                    .withParams(comment.getId(), username, type.toString(), entryID));
        }
        else {
            persistedComment.setText(comment.getText());
        }
    }
    
    @Transactional
    public void deleteComment(EntryType type, Integer entryID, Integer commentID) throws NullValueException, EntryNotFoundException, UserNotFoundException, CommentNotFoundException {
        if (entryID == null || commentID == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        Comment persistedComment = em.find(Comment.class, new BigDecimal(commentID));
        if (persistedComment == null || !persistedComment.getEntry().getId().equals(new BigDecimal(entryID))) {
            throw new CommentNotFoundException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.COMMENT_NOT_FOUND).withParams(commentID, type.toString(), entryID));
        }
        else {
            em.remove(persistedComment);
        }
    }
    
    @Transactional
    public void deleteComment(EntryType type, Integer entryID, Integer commentID, String username) throws NullValueException, EntryNotFoundException, UserNotFoundException, CommentNotFoundException {
        if (entryID == null || commentID == null) {
            throw new NullValueException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ENTRY_NULL));
        }
        
        Comment persistedComment = em.find(Comment.class, new BigDecimal(commentID));
        if (persistedComment == null || !persistedComment.getEntry().getId().equals(new BigDecimal(entryID)) || 
                !persistedComment.getUser().getUsername().equals(username)) {
            throw new CommentNotFoundException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.COMMENT_OF_USER_NOT_FOUND)
                    .withParams(commentID, username, type.toString(), entryID));
        }
        else {
            em.remove(persistedComment);
        }
    }
}
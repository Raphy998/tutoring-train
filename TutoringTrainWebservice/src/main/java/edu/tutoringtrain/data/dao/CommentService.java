/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import com.mysema.query.jpa.EclipseLinkTemplates;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import edu.tutoringtrain.data.EntryType;
import edu.tutoringtrain.data.ResettableEntryProp;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.EntryNotFoundException;
import edu.tutoringtrain.data.exceptions.SubjectNotActiveException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.data.search.entry.EntryQueryGenerator;
import edu.tutoringtrain.data.search.entry.EntrySearch;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.entities.QEntry;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigDecimal;
import java.text.ParseException;
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
}
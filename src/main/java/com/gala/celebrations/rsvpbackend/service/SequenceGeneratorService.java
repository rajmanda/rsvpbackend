package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    // public int generateNextRsvpId(){
    //     Sequence counter = mongoOperations.findAndModify(
    //             query(where("_id").is("sequence")),
    //             new Update().inc("sequence", 1),
    //             options().returnNew(true).upsert(true),
    //             Sequence.class);
    //     return counter.getSequence();
    // }
    public int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        Sequence sequence = mongoOperations.findAndModify(query, update, options, Sequence.class);
        return sequence != null ? sequence.getSequence() : 1;
    }
}
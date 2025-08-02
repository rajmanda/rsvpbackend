package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.entity.Sequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final ReactiveMongoOperations reactiveMongoOperations;

    public Mono<Integer> getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("sequence", 1);
        
        return reactiveMongoOperations.findAndModify(
                query,
                update,
                new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true).upsert(true),
                Sequence.class
        ).map(Sequence::getSequence)
        .defaultIfEmpty(1); // Return 1 if no sequence found (first time)
    }
}
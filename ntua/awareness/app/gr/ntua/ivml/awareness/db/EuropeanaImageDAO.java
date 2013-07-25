package gr.ntua.ivml.awareness.db;



import gr.ntua.ivml.awareness.persistent.EuropeanaImage;

import gr.ntua.ivml.awareness.util.MongoDB;


import org.bson.types.ObjectId;

import com.google.code.morphia.dao.BasicDAO;


public class EuropeanaImageDAO extends BasicDAO<EuropeanaImage, ObjectId> {
    public EuropeanaImageDAO(  ) {
        super(EuropeanaImage.class, MongoDB.getDS());
    }
    
   }

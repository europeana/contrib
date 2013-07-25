package gr.ntua.ivml.awareness.util;

import gr.ntua.ivml.awareness.db.StoryObjectDAO;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.persistent.StoryObjectPlaceHolder;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EuropeanaResourceValidator implements ConstraintValidator<HasEuropeanaResource, List<StoryObjectPlaceHolder>> {

	private StoryObjectDAO storyD;
	@Override
	public void initialize(HasEuropeanaResource arg0) {
		storyD = new StoryObjectDAO();
	}

	@Override
	public boolean isValid(List<StoryObjectPlaceHolder> arg0, ConstraintValidatorContext arg1) {
		List<StoryObject> ls = storyD.getStoryObjectsByPlaceHolders((ArrayList<StoryObjectPlaceHolder>) arg0);
		if(ls != null){
			for(StoryObject obj : ls){
				if(obj != null){
					if(obj.getSosource().equalsIgnoreCase("europeana"))
					return true;
				}
			}
		}
		
		return false;
	}

}

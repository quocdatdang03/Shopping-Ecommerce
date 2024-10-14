package com.shoppingbackend.admin.category.validator;

import com.shopping.common.entity.Category;
import com.shoppingbackend.admin.category.customAnnotation.UniqueName;
import com.shoppingbackend.admin.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        if(name==null || name.isEmpty())
            return false;

        Category category = categoryRepository.findCategoryByName(name);
        if(category!=null)
            return false;

        return true;
    }
}

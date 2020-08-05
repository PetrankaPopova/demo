package diplomna.validation.category;

import diplomna.model.bindingmodel.CategoryEditBindingModel;
import diplomna.repository.CategoryRepository;
import diplomna.validation.ValidationConstants;
import diplomna.validation.annotation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

@Validator
public class CategoryEditValidator implements org.springframework.validation.Validator{

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryEditValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryEditBindingModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CategoryEditBindingModel categoryEditBindingModel = (CategoryEditBindingModel) o;

        //Category category = this.categoryRepository.findByName(categoryEditBindingModel.getName()).orElse(null);

        if (categoryEditBindingModel.getCategoryName().length() < 3) {
            errors.rejectValue(
                    "name",
                    ValidationConstants.NAME_LENGTH,
                    ValidationConstants.NAME_LENGTH
            );
        }

        if (this.categoryRepository.findByCategoryName(categoryEditBindingModel.getCategoryName()).isPresent()) {
            errors.rejectValue(
                    "name",
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryEditBindingModel.getCategoryName()),
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryEditBindingModel.getCategoryName())
            );
        }
    }
}

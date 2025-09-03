package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.MoveCategoryRequest;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Category;
import backend_for_react.backend_for_react.repository.CategoryRepository;
import backend_for_react.backend_for_react.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public PageResponse<CategoryResponse> findAll(String keyword, String sort, int page, int size) {
        Sort order = Sort.by(Sort.Direction.ASC,"id");
        if(sort !=null && !sort.isEmpty()){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = Sort.by(Sort.Direction.ASC,columnName);
                }else {
                    order = Sort.by(Sort.Direction.DESC,columnName);
                }
            }
        }
        int pageNo =0;
        if(page > 0){
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo,size,order);
        Page<Category> categories = null;
        if(keyword == null || keyword.isEmpty()){
            categories = categoryRepository.findAll(pageable);
        }else {
            keyword = "%"+keyword.toLowerCase()+"%";
            categories = categoryRepository.searchByKeyword(keyword,pageable);
        }
        PageResponse response = getCategoryPageResponse(pageNo,size,categories);
        return response;
    }

    @Override
    public List<CategoryResponse> findAllWithouPagination(){
        List<CategoryResponse> categoryList = categoryRepository.findAll().stream()
                .filter(category -> category.getStatus().equals(Status.ACTIVE) && category.getParent()==null ) // Lọc trước
                .map(this::getCategoryResponse) // Gọi hàm chuyển đổi
                .toList();
        return categoryList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(List<CategoryCreationRequest> requests) {
        for (CategoryCreationRequest req : requests){
            Category parent = null;
            if(req.getParentId() != null){
                parent = categoryRepository.findById(req.getParentId())
                        .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.CATEGORY_NOT_FOUND));
            }
            saveChildrenCategory(parent,req);
        }
    }

    private void saveChildrenCategory(Category parent, CategoryCreationRequest request){
        Category newCategory = new Category();
        newCategory.setStatus(Status.ACTIVE);
        newCategory.setName(request.getName());
        newCategory.setParent(parent);
        categoryRepository.save(newCategory);
        if(request.getChildCategories() != null){
            for(CategoryCreationRequest child : request.getChildCategories()){
                saveChildrenCategory(newCategory,child);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(MoveCategoryRequest request){
        Category currentCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.CATEGORY_NOT_FOUND));
        if(request.getCategoryParentId() == null){
            currentCategory.setParent(null);
            categoryRepository.save(currentCategory);
        }else {
            Category parentCategory = categoryRepository.findById(request.getCategoryParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.CATEGORY_NOT_FOUND));
            currentCategory.setParent(parentCategory);
            categoryRepository.save(currentCategory);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(CategoryUpdateRequest req) {
        Category category = categoryRepository.findById(req.getId()).orElseThrow(()-> new EntityNotFoundException("Category not found"));
        if (req.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + req.getParentId()));
            category.setParent(parentCategory);
        }
        category.setName(req.getName());
        category.setStatus(req.getStatus());
        categoryRepository.save(category);
    }

    private void updateChildrentStatus(Category parent , Status status){
        List<Category> childCategories = categoryRepository.findCategoriesByParentAndStatus(parent,Status.ACTIVE);
        for (Category child : childCategories){
            child.setStatus(status);
            categoryRepository.save(child);
            updateChildrentStatus(child,status);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.CATEGORY_NOT_FOUND));
        updateChildrentStatus(category,Status.INACTIVE);
        category.setStatus(Status.INACTIVE);
        categoryRepository.save(category);
        updateChildrentStatus(category,Status.INACTIVE);
    }


    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not found"));
        return getCategoryResponse(category);
    }
    @Override
    public List<Category> getAllParentCategories(Long categoryId) {
        List<Category> parentCategories = new ArrayList<>();
        Category current = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        parentCategories.add(0,current);
        while (current.getParent() != null) {
            current = current.getParent();
            parentCategories.add(0, current); // Thêm vào đầu để giữ đúng thứ tự
        }

        return parentCategories;
    }
    private CategoryResponse getCategoryResponse(Category category){
        List<CategoryResponse> categoryResponses = categoryRepository.findCategoriesByParentAndStatus(category , Status.ACTIVE).stream().map(
                this::getCategoryResponse
        ).toList();
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .childCategory(categoryResponses)
                .status(category.getStatus())
                .createAt(category.getCreatedAt())
                .build();
    }
    private PageResponse<CategoryResponse> getCategoryPageResponse(int page, int size, Page<Category> categories) {
        List<CategoryResponse> categoryList = categories.stream()
                .filter(category -> category.getStatus().equals(Status.ACTIVE) && category.getParent()==null ) // Lọc trước
                .map(this::getCategoryResponse) // Gọi hàm chuyển đổi
                .toList();

        PageResponse<CategoryResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(categories.getTotalElements());
        response.setTotalPages(categories.getTotalPages());
        response.setData(categoryList);
        return response;
    }


}

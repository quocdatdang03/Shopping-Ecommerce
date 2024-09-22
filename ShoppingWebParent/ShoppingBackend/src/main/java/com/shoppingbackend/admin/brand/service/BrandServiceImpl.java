package com.shoppingbackend.admin.brand.service;

import com.shopping.common.entity.Brand;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.brand.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements BrandService{

    public static final int BRAND_NUMBER_PER_PAGE = 5;

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<Brand> listAllBrands() {
        return (List<Brand>) brandRepository.findAll();
    }

    @Override
    public Page<Brand> listBrandsByPage(Integer pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber-1, BRAND_NUMBER_PER_PAGE, sort);

        if(keyword!=null && !keyword.isEmpty())
            return brandRepository.searchByKeyword(keyword, pageable);

        return brandRepository.findAll(pageable);
    }

    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand getBrandById(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        }
        catch (NoSuchElementException e) {
            throw new BrandNotFoundException("Count not find any brand with id : "+id);
        }
    }

    @Override
    public void deleteBrandById(Integer id) throws BrandNotFoundException {
        Integer countResult = brandRepository.countById(id);

        // Check not find any brand
        if(countResult==null || countResult==0)
            throw new BrandNotFoundException("Count not find any brand with id: "+id);

        // delete :
        brandRepository.deleteById(id);
    }


}

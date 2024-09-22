package com.shoppingbackend.admin.brand.service;

import com.shopping.common.entity.Brand;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    public List<Brand> listAllBrands();
    public Page<Brand> listBrandsByPage(Integer pageNumber ,String sortField , String sortDir, String keyword);
    public Brand saveBrand(Brand brand);
    public Brand getBrandById(Integer id) throws BrandNotFoundException;
    public void deleteBrandById(Integer id) throws BrandNotFoundException;
}

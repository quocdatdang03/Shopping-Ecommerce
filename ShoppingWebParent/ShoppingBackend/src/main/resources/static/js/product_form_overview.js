const brandsDropDown = $('#brandList');
const categoriesDropDown = $('#categoryList');
const categoryId = $('#categoryId');

// using Jquery:
$(document).ready(function () {

    // handle set alias by default is product name (replace spaces by dashes - ) by when typing product Name:
    const productNameInput = $('#productName');
    const productAliasInput = $('#productAlias');

    productNameInput.on('input', function() {
        productAliasInput.empty();
        let productNameValue = productNameInput.val().trim();

        productAliasInput.val(productNameValue.replaceAll(" ", "-"));
    })


    // handle click brand dropdown show associated categories:
    brandsDropDown.on('change', function() {
        // clear brandsDropDown content in advanced :
        categoriesDropDown.empty();

        getAssociatedCategories();
    })

    let editMode = false;
    if(categoryId!=null && categoryId.length > 0)
    {
        editMode = true;
    }
    // Nếu không phải edit (tức là create mode) thì mới cho load data lần đầu cho categoriesDropDown
    if(!editMode) {
        // call first time to load associatedCategories to categoriesDropDown
        getAssociatedCategories();
    }

})

// handle show associated categories of selected brand :
function getAssociatedCategories() {
    let brandId = brandsDropDown.val();
    let url = brandModuleURL+"/"+brandId+"/categories";

    // ajax call GET request:
    $.get(url, function (response) {
        response.forEach(function (item) {
            const categoryId = item.id;
            const categoryName = item.name;
            // add associated categories of brand into categoriesDropDown :
            // Cách 1:
            // categoriesDropDown.append("<option value='"+categoryId+"'>"+categoryName+"</option>");
            // Cách 2 ngắn gọn hơn:
            $("<option>").val(categoryId).text(categoryName).appendTo(categoriesDropDown)
        })
    })
}
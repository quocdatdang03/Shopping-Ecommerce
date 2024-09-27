// // using Jquery:
// $(document).ready(function () {
//     const brandsDropDown = $('#brandList');
//     const categoriesDropDown = $('#categoryList');
//     const defaultImageThumbnailSrc = "[[@{/images/img-thumbnail-default.jpg}]]";
//     var extraImagesCount = 0;
//
//     // activate rich text editor:
//     $('#shortDescription').richText();
//     $('#fullDescription').richText();
//
//     // handle set alias by default is product name (replace spaces by dashes - ) by when typing product Name:
//     const productNameInput = $('#productName');
//     const productAliasInput = $('#productAlias');
//
//     productNameInput.on('input', function() {
//         productAliasInput.empty();
//         let productNameValue = productNameInput.val().trim();
//
//         productAliasInput.val(productNameValue.replaceAll(" ", "-"));
//     })
//
//
//     // Handle Change Image when upload input type file:
//     //      Handle when click image thumbnail -> input image file click
//     $('#imgThumbnail').on('click', function() {
//         $('#inputImageFile').click();
//     })
//
//     $('#inputImageFile').change(function() {
//         // Nếu fileSize không thỏa mãn thì không làm gì cả
//         if(checkFileSize(this)===false) {
//
//             return "";
//         }
//         showImageThumbnail(this);
//     })
//
//     $('input[name="inputExtraImageFile"]').each(function(index) {
//         extraImagesCount++;
//         $(this).on('change', function() {
//             showExtraImageThumbnail(this, index);
//         })
//     })
//
//
//     // show chosen categories :
//     const categories  = $('#categories')
//     const chosenCategories = $('#chosenCategories');
//
//     categories.on('change', function () {
//         // clear chosenCategories in advanced :
//         chosenCategories.empty();
//
//         let selectedCategory = categories.children("option:selected");
//         selectedCategory.each(function () {
//             let categoryId = $(this).val()
//             // replace -- to emtpty :
//             let categoryName = $(this).text().replace(/--/g,"")
//
//             chosenCategories.append("<span class='badge text-bg-dark mx-1'>"+categoryName+"</span>")
//         })
//
//     })
//
//
//     // handle click brand dropdown show associated categories:
//     brandsDropDown.on('change', function() {
//         // clear brandsDropDown content in advanced :
//         categoriesDropDown.empty();
//
//         getAssociatedCategories();
//     })
//
//     // call first time to load associatedCategories to categoriesDropDown
//     getAssociatedCategories();
//
// })
//
// // handle show associated categories of selected brand :
// function getAssociatedCategories() {
//     let brandId = brandsDropDown.val();
//     let url = "[[@{/brands/}]]"+brandId+"/categories";
//
//     // ajax call GET request:
//     $.get(url, function (response) {
//         response.forEach(function (item) {
//             const categoryId = item.id;
//             const categoryName = item.name;
//             // add associated categories of brand into categoriesDropDown :
//             // Cách 1:
//             // categoriesDropDown.append("<option value='"+categoryId+"'>"+categoryName+"</option>");
//             // Cách 2 ngắn gọn hơn:
//             $("<option>").val(categoryId).text(categoryName).appendTo(categoriesDropDown)
//         })
//     })
// }
//
// // Check file size:
// function checkFileSize(fileInput) {
//     // Lấy ra file size
//     var fileSize = fileInput.files[0].size
//     alert(fileSize)
//     // check if fileSize > 1MB (1048567) -> not accept:
//     if(fileSize>1048567)
//     {
//         fileInput.setCustomValidity("file size must less than 1MB!")
//         fileInput.reportValidity()
//         return false
//     }
//     else {
//         fileInput.setCustomValidity("")
//         return true;
//     }
// }
//
// // Handle Change Image when upload input type file:
// function showImageThumbnail(fileInput) {
//     // Lấy ra đối tượng FileList :
//     var file = fileInput.files[0];
//
//     var fileReader = new FileReader();
//     fileReader.readAsDataURL(file)
//
//     fileReader.onload = function (e) {
//         $('#imgThumbnail').attr("src", e.target.result)
//     }
// }
//
// // Handle when click into image, it will show dialog of file input to choose file:
// function handleShowInputFileDialog(index) {
//     $('#inputExtraImageFile'+index).click();
// }
//
// // Handle Change Extra Image when upload input type file:
// function showExtraImageThumbnail(fileInput, index) {
//     // if file size is not eligible (false) -> do nothing:
//     if(checkFileSize(fileInput)===false)
//         return "";
//
//     // Lấy ra đối tượng FileList :
//     var file = fileInput.files[0];
//
//     var fileReader = new FileReader();
//     fileReader.readAsDataURL(file)
//
//     fileReader.onload = function (e) {
//         $('#extraImgThumbnail'+index).attr("src", e.target.result)
//     }
//
//     // Nếu là extra images cuối cùng thì mới addNextExtraImage :
//     if(index >= extraImagesCount-1)
//         addNextExtraImage(index+1);
// }
//
// // add next extra image:
// function addNextExtraImage(index) {
//     const htmlExtraImage = `
//             <div class="col text-center" id="divExtraImage${index}">
//                 <div id="extraImageHeader${index}" class="d-flex justify-content-center align-items-center">
//                      <label class="form-label badge text-bg-secondary fs-5">Extra Image #${index+1}</label>
//                 </div>
//                 <div class="image-container mt-1 mb-2">
//                     <img id="extraImgThumbnail${index}" onclick="handleShowInputFileDialog(${index})" class="img-fluid" src="${defaultImageThumbnailSrc}" alt="" style="cursor: pointer;"/>
//                 </div>
//                 <input name="inputExtraImageFile" id="inputExtraImageFile${index}" type="file" class="form-control" onchange="showExtraImageThumbnail(this, ${index})" accept="image/png, image/jpeg"/>
//              </div>
//         `;
//
//     // Chỉ hiển thị button remove với các extra image không phải là cuối cùng
//     // add button remove vào extra image trước extra image vừa mới được thêm vào
//     const htmlBtnRemove = `
//             <div class="btn fa-solid fa-trash fa-2x text-danger"
//                 onclick="removeExtraImage(${index - 1})"
//                 title="Remove this image">
//             </div>
// 	    `;
//
//
//
//     $('#divProductImage').append(htmlExtraImage);
//
//     // add button remove vào extra image trước extra image vừa mới được thêm vào
//     $("#extraImageHeader" + (index - 1)).append(htmlBtnRemove);
//
//     extraImagesCount++;
// }
//
// // remove extra image
// function removeExtraImage(index) {
//     $("#divExtraImage" + index).remove();
// }
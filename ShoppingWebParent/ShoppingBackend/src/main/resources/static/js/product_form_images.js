var extraImagesCount = 0;

// using Jquery:
$(document).ready(function () {

    // Handle Change Image when upload input type file:
    //      Handle when click image thumbnail -> input image file click
    $('#imgThumbnail').on('click', function() {
        $('#inputImageFile').click();
    })
    $('#inputImageFile').change(function() {
        // Nếu fileSize không thỏa mãn thì không làm gì cả
        if(checkFileSize(this)===false) {

            return "";
        }
        showImageThumbnail(this);
    })

    $('input[name="inputExtraImageFile"]').each(function(index) {
        extraImagesCount++;
        $(this).on('change', function() {
            showExtraImageThumbnail(this, index);
        })
    })

})

// Check file size:
function checkFileSize(fileInput) {
    // Lấy ra file size
    var fileSize = fileInput.files[0].size
    alert(fileSize)
    // check if fileSize > 1MB (1048567) -> not accept:
    if(fileSize>1048567)
    {
        fileInput.setCustomValidity("file size must less than 1MB!")
        fileInput.reportValidity()
        return false
    }
    else {
        fileInput.setCustomValidity("")
        return true;
    }
}

// Handle Change Image when upload input type file:
function showImageThumbnail(fileInput) {
    // Lấy ra đối tượng FileList :
    var file = fileInput.files[0];

    var fileReader = new FileReader();
    fileReader.readAsDataURL(file)

    fileReader.onload = function (e) {
        $('#imgThumbnail').attr("src", e.target.result)
    }
}

// Handle when click into image, it will show dialog of file input to choose file:
function handleShowInputFileDialog(index) {
    $('#inputExtraImageFile'+index).click();
}

// Handle Change Extra Image when upload input type file:
function showExtraImageThumbnail(fileInput, index) {
    // if file size is not eligible (false) -> do nothing:
    if(checkFileSize(fileInput)===false)
        return "";

    // Lấy ra đối tượng FileList :
    var file = fileInput.files[0];

    // ------ for edit Mode: in case change existing one
    // Lấy ra file name
    var fileName = file.name;
    // get ra input file hidden đang lưu trữ image name:
    var imageNameHiddenField = $('#imageName'+index);
    // check nếu tồn tại thì mới set val cho nó:
    if(!imageNameHiddenField.empty())
        imageNameHiddenField.val(fileName)
    // ------ for edit Mode

    if(fileName.length)
        $('#imageName'+index).val(fileName)

    var fileReader = new FileReader();
    fileReader.readAsDataURL(file)

    fileReader.onload = function (e) {
        $('#extraImgThumbnail'+index).attr("src", e.target.result)
    }

    // Nếu là extra images cuối cùng thì mới addNextExtraImage :
    if(index >= extraImagesCount-1)
        addNextExtraImage(index+1);
}

// add next extra image:
function addNextExtraImage(index) {
    const htmlExtraImage = `
            <div class="col text-center" id="divExtraImage${index}">
                <div id="extraImageHeader${index}" class="d-flex justify-content-center align-items-center">
                     <label class="form-label badge text-bg-secondary fs-5">Extra Image #${index+1}</label>
                </div>
                <div class="image-container mt-1 mb-2">
                    <img id="extraImgThumbnail${index}" onclick="handleShowInputFileDialog(${index})" class="img-fluid" src="${defaultImageThumbnailSrc}" alt="" style="cursor: pointer;"/>
                </div>
                <input name="inputExtraImageFile" id="inputExtraImageFile${index}" type="file" class="form-control" onchange="showExtraImageThumbnail(this, ${index})" accept="image/png, image/jpeg"/>
             </div>
        `;

    // Chỉ hiển thị button remove với các extra image không phải là cuối cùng
    // add button remove vào extra image trước extra image vừa mới được thêm vào
    const htmlBtnRemove = `
        <div class="btn fa-solid fa-trash fa-2x text-danger"
            onclick="removeExtraImage(${index - 1})"
            title="Remove this image">
        </div>
    `;


    $('#divProductImage').append(htmlExtraImage);

    // add button remove vào extra image trước extra image vừa mới được thêm vào
    $("#extraImageHeader" + (index - 1)).append(htmlBtnRemove);

    extraImagesCount++;
}

// remove extra image
function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}

const btnAddMoreDetail = $('#btnAddMoreDetail');
const divProductDetails = $('#divProductDetails');

$(document).ready(function () {
    btnAddMoreDetail.on('click', function () {
        const allDivDetailsLength = $('.detail-info').length;
        addNextDetail(allDivDetailsLength);
    })
})

function addNextDetail(index) {
    const allDivDetails = $('.detail-info')

    let htmlDetail = `
        <div class="detail-info d-flex align-items-center mb-3" id="divDetail${index}">
            <div class="row row-cols-2 w-100 g-sm-3">
                <div class="col d-flex align-items-center">
                    <label class="me-2">Name: </label>
                    <input type="text" name="detailName" class="form-control"/>
                </div>
                <div class="col d-flex align-items-center">
                    <label class="me-2">Value: </label>
                    <input type="text" name="detailValue" class="form-control" />
                </div>
            </div>
        </div>
    `;

    // append to divProductDetails :
    divProductDetails.append(htmlDetail);

    //get previous divDetails :
    const previousDivDetail = allDivDetails.last();
    const previousDivDetailId = previousDivDetail.attr("id");

    let htmlBtnRemove = `
         <div class="btn text-danger" onclick="removeDivDetail('${previousDivDetailId}')">
            <i class="fa-solid fa-trash fa-2x"></i>
         </div>
    `;

    // append btn remove for previous divDetail which is not be last divDetail :
    previousDivDetail.append(htmlBtnRemove);

}

function removeDivDetail(idDivDetail) {
    $("#"+idDivDetail).remove();
}

function removeDivDetailByIndex(index) {
    $("#divDetail"+index).remove();
}
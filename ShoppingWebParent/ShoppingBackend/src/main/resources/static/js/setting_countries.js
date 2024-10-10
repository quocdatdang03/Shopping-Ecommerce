var btnLoadCountryList;
var dropDownListCountry;
var btnAddCountry;
var btnDeleteCountry;
var btnUpdateCountry;
// get input countryName and countryCode:
var inputCountryName = $('#inputCountryName');
var inputCountryCode = $('#inputCountryCode');

$(document).ready(function () {
    btnLoadCountryList = $("#btnLoadCountryList");
    dropDownListCountry = $("#dropDownListCountry");
    btnAddCountry = $("#btnAddCountry");
    btnDeleteCountry = $("#btnDeleteCountry");
    btnUpdateCountry = $("#btnUpdateCountry");

    // handle load country list when click btn load:
    btnLoadCountryList.on('click', function () {
        loadCountryList();
    })

    // handle when choosing item in drop down country list :
    dropDownListCountry.on('change', function() {
        handleWhenClickItemOfDropDownCountryList();
    })

    // handle when click btn Add or btn New
    btnAddCountry.on('click', function() {
        if(btnAddCountry.text() === 'Add') {
            handleWhenClickBtnAdd();
        }
        else {
            handleChangeFormToNewState();
        }
    })

    // handle when click btn update:
    btnUpdateCountry.on('click', function() {
        handleWhenClickBtnUpdate();
    })

    // handle when click btn delete:
    btnDeleteCountry.on('click', function() {
        handleWhenClickBtnDelete();
    })
})


function handleWhenClickItemOfDropDownCountryList() {
    // get selected option:
    const selectedOption = $('#dropDownListCountry option:selected');

    let countryName = selectedOption.text();
    // change label of countryName -> selected country name:
    const labelCountryName = $('#labelCountryName');
    labelCountryName.text("Selected Country Name");


    // value option có dạng như sau : index-countryCode
    // Do đó ta phải lấy ra countryCode
    let countryCode = selectedOption.val().split("-")[1];
    inputCountryName.val(countryName);
    inputCountryCode.val(countryCode);

    // change text of btn Add -> btn New:
    btnAddCountry.text("New");

    // enable btn update and delete
    btnUpdateCountry.prop('disabled',false);
    btnDeleteCountry.prop('disabled',false);
}

function handleWhenClickBtnAdd() {
    // Add country

    let requestURL = contextPath+"countries/save";
    // get value of inputCountryName and inputCountryCode :
    let countryName = inputCountryName.val().trim();
    let countryCode = inputCountryCode.val().trim();

    let jsonData = {name:countryName, code:countryCode};

    // use ajax call to send request with method POST to Server
    $.ajax({
       type : "POST",
       url : requestURL,
       beforeSend: function (xhr) {
           xhr.setRequestHeader(csrfHeaderName, csrfValue);
       },
       data : JSON.stringify(jsonData), // convert java object to JSON string
        contentType : "application/json"
    }).done(function(countryId) {
        // show Toast Message :
        showToastMessage("Add new country successfully!");
        handleWhenAddNewCountrySuccess(countryId, countryName, countryCode);
    }).fail(function(error) {
        showToastMessage("ERROR: Could not connect to Server");
    });
}

function handleWhenAddNewCountrySuccess(countryId, countryName, countryCode) {
    let optionValue = countryId+"-"+countryCode;

    // add New Country to DropDown List
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownListCountry);

    // select new country just added to dropDownListCountry:
    $("#dropDownListCountry option[value='"+optionValue+"']").prop('selected',true);

    // clear text of inputCountryName and inputCountryCode :
    inputCountryName.val("").focus();
    inputCountryCode.val("");
}

// handle when click btn Update:
function handleWhenClickBtnUpdate() {
    // Update country

    let requestURL = contextPath+"countries/save";
    // get value of inputCountryName and inputCountryCode :
    let countryName = inputCountryName.val().trim();
    let countryCode = inputCountryCode.val().trim();
    let selectedOption = $('#dropDownListCountry option:selected');
    let countryId = selectedOption.val().split("-")[0];

    let jsonData = {id: countryId, name:countryName, code:countryCode};

    // use ajax call to send request with method POST to Server
    $.ajax({
        type : "POST",
        url : requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data : JSON.stringify(jsonData), // convert java object to JSON string
        contentType : "application/json"
    }).done(function(countryId) {
        // show Toast Message :
        showToastMessage("Update country successfully!");
        // update text and value of updated country in DropDownListCountry:
        selectedOption.text(countryName);
        selectedOption.val(countryId+"-"+countryCode);
        handleWhenClickBtnNew();
    }).fail(function(error) {
        showToastMessage("ERROR: Could not connect to Server");
    });
}


// handle when click btn Delete
function handleWhenClickBtnDelete() {
    // Delete country:

    // get country id of selected option in dropdownListCountry:
    let selectedOption = $('#dropDownListCountry option:selected');
    let countryId = selectedOption.val().split("-")[0];
    let countryName = selectedOption.text();
    let optionValue = selectedOption.val();


    const requestURL = contextPath+"countries/delete/"+countryId;

    $.ajax({
        type : "DELETE",
        url : requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        // remove from dropDownCountryList
        $("#dropDownListCountry option[value='"+optionValue+"']").remove();
        handleWhenClickBtnNew();
        showToastMessage("Delete country with name:"+countryName+" successfully");
    }).fail(function(error) {
        showToastMessage("ERROR: Could not connect to Server");
    })
}

function loadCountryList() {
    // send request to Server by Ajax call :
    const requestURL = contextPath+'countries/list';

    // send request method GET:
    $.get(requestURL, function(responseJSON) {
        // before add country to dropDown -> clear content of dropdown in advance
        dropDownListCountry.empty();

        $.each(responseJSON, function (index, item) {
            let optionValue = item.id+"-"+item.code;
            let optionName = item.name;

            $("<option>").val(optionValue).text(optionName).appendTo(dropDownListCountry);
        })
    })
        .done(function() { // done will be called when send request Success (200 OK)
            // change text of button Load:
            btnLoadCountryList.text("Refresh Country List");
            showToastMessage("Load country list successfully");
        })
        .fail(function(error) {
            showToastMessage("ERROR: Could not connect to Server");
        })

}

function showToastMessage(message) {
    const toastMessage = $('#toastMessage');
    const toastMessageText = $('#toastMessageText');

    // show toast message
    toastMessage.removeClass("d-none").addClass("show");

    // Tự động ẩn thông báo sau 5 giây (tùy chọn)
    setTimeout(() => {
        toastMessage.removeClass('show').addClass('d-none');
    }, 5000);

    toastMessageText.text(message)
}
var btnLoadCountryListForStates;
var dropDownListCountryForStates;
var dropDownListState;
var btnAddState;
var btnDeleteState;
var btnUpdateState;
// get input inputStateName:
var inputStateName = $('#inputStateName');

$(document).ready(function() {

    btnLoadCountryListForStates = $("#btnLoadCountryListForStates");
    dropDownListCountryForStates = $("#dropDownListCountryForStates");
    dropDownListState = $('#dropDownListState');
    btnAddState = $("#btnAddState");
    btnDeleteState = $("#btnDeleteState");
    btnUpdateState = $("#btnUpdateState");

    // handle load country list when click btn load:
    btnLoadCountryListForStates.on('click', function () {
        loadCountryListForStates();
    })

    // hanlde load state list when change dropDownListState:
    dropDownListCountryForStates.on('change', function() {
        handleLoadStateList();
    })

    // handle when click item of dropDownListState:
    dropDownListState.on('change', function() {
        handleWhenClickItemOfDropDownStateList();
    });

    // handle when click btn Add or btn New
    btnAddState.on('click', function() {
        if(btnAddState.text() === 'Add') {
            handleWhenClickBtnAddState();
        }
        else {
            handleWhenClickBtnNewState();
        }
    })

    // handle when click btn update:
    btnUpdateState.on('click', function() {
        handleWhenClickBtnUpdateState();
    })

    // handle when click btn delete:
    btnDeleteState.on('click', function() {
        handleWhenClickBtnDeleteState();
    })
});

function handleLoadStateList() {
    // get country id from selected dropDownCountryList :
    const selectedCountry = $('#dropDownListCountryForStates option:selected');
    const countryId = selectedCountry.val();
    // send request to Server by Ajax call :
    const requestURL = contextPath+'states/list_by_country/'+countryId;

    // send request method GET:
    $.get(requestURL, function(responseJSON) {
        // before add state to dropDown -> clear content of dropdown in advance
        dropDownListState.empty();

        $.each(responseJSON, function (index, item) {
            let optionValue = item.id;
            let optionName = item.name;

            $("<option>").val(optionValue).text(optionName).appendTo(dropDownListState);
        })
    })
        .done(function() { // done will be called when send request Success (200 OK)
            // change text of button Load:
            showToastMessage("All states have been loaded for country: "+selectedCountry.text());
            handleChangeFormToNewState();
        })
        .fail(function(error) {
            showToastMessage("ERROR: Could not connect to Server");
        })
}


function handleChangeFormToNewState() {
    // Hủy bỏ selected ở option được selected:
    const selectedOption = $('#dropDownListState option:selected');
    selectedOption.prop("selected", false);

    // change text btn New -> Add :
    btnAddState.text("Add");

    // disable btn update and delete
    btnUpdateState.prop('disabled',true);
    btnDeleteState.prop('disabled',true);

    // empty and focus inputStateName :
    inputStateName.val("").focus();
}

function handleWhenClickItemOfDropDownStateList() {
    // get selectedState option:
    const selectedStateOption = $('#dropDownListState option:selected');

    let stateName = selectedStateOption.text();
    let stateId = selectedStateOption.val();

    // change label of stateName -> selected state name:
    const labelStateName = $('#labelStateName');
    labelStateName.text("Selected State Name");


    inputStateName.val(stateName);

    // change text of btn Add -> btn New:
    btnAddState.text("New");

    // enable btn update and delete
    btnUpdateState.prop('disabled',false);
    btnDeleteState.prop('disabled',false);
}

function handleWhenClickBtnNewState() {
    // Hủy bỏ selected ở option được selected:
    const selectedOption = $('#dropDownListState option:selected');
    selectedOption.prop("selected", false);

    // change text btn New -> Add :
    btnAddState.text("Add");

    // disable btn update and delete
    btnUpdateState.prop('disabled',true);
    btnDeleteState.prop('disabled',true);

    // empty and focus inputStateName :
    inputStateName.val("").focus();
}

function handleWhenClickBtnAddState() {
    // Add State

    // get selected country option:
    const selectedCountryOption = $('#dropDownListCountryForStates option:selected');

    let requestURL = contextPath+"states/save";
    // get value of inputStateName:
    let stateName = inputStateName.val().trim();

    let countryId = selectedCountryOption.val();
    let countryName = selectedCountryOption.text();

    let jsonData = {
        name:stateName,
        country: {
            id:countryId,
            name:countryName
        }
    };

    // use ajax call to send request with method POST to Server
    $.ajax({
        type : "POST",
        url : requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue); // add thêm csrf vào không thì nó sẽ không gửi method Post được vì Spring Security đã block
        },
        data : JSON.stringify(jsonData), // convert java object to JSON string
        contentType : "application/json"
    }).done(function(stateId) {
        // show Toast Message :
        showToastMessageForStates("Add new state successfully!");
        handleWhenAddNewStateSuccess(stateId, stateName);
    }).fail(function(error) {
        showToastMessageForStates("ERROR: Could not connect to Server");
    });
}

function handleWhenAddNewStateSuccess(stateId, stateName) {
    // add New State to DropDown List
    $("<option>").val(stateId).text(stateName).appendTo(dropDownListState);

    // select new country just added to dropDownListState:
    $("#dropDownListState option[value='"+stateId+"']").prop('selected',true);

    // clear text of inputStateName :
    inputStateName.val("").focus();
}


// handle when click btn Update:
function handleWhenClickBtnUpdateState() {
    // Update states:

    let requestURL = contextPath+"states/save";

    // get selected country option :
    const selectedCountryOption = $('#dropDownListCountryForStates option:selected');
    // get selected state option :
    const selectedStateOption = $('#dropDownListState option:selected');

    // get value of inputStateName :
    let stateId = selectedStateOption.val();
    let stateName = inputStateName.val().trim();
    let countryId = selectedCountryOption.val();
    let countryName = selectedCountryOption.text();

    let jsonData = {
        id:stateId,
        name:stateName,
        country: {
            id:countryId,
            name:countryName
        }
    };

    // use ajax call to send request with method POST to Server
    $.ajax({
        type : "POST",
        url : requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data : JSON.stringify(jsonData), // convert java object to JSON string
        contentType : "application/json"
    }).done(function(stateId) {
        // show Toast Message :
        showToastMessageForStates("Update state successfully!");
        // update text and value of updated state in dropDownListState:
        selectedStateOption.text(stateName);
        selectedStateOption.val(stateId);
        handleChangeFormToNewState();
    }).fail(function(error) {
        showToastMessageForStates("ERROR: Could not connect to Server");
    });
}


// handle when click btn Delete
function handleWhenClickBtnDeleteState() {
    // Delete state:

    // get state id of selected state option in dropdownListState:
    let selectedStateOption = $('#dropDownListState option:selected');
    let stateId = selectedStateOption.val();
    let stateName = selectedStateOption.text();

    const requestURL = contextPath+"states/delete/"+stateId;

    // use ajax call to send request with method POST to Server
    $.ajax({
        type : "DELETE",
        url : requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        // remove from dropDownListState
        $("#dropDownListState option[value='"+stateId+"']").remove();
        handleChangeFormToNewState();
        showToastMessageForStates("Delete state with name:"+stateName+" successfully");
    }).fail(function(error) {
        showToastMessageForStates("ERROR: Could not connect to Server");
    })
}


function loadCountryListForStates() {
    // send request to Server by Ajax call :
    const requestURL = contextPath+'countries/list';

    // send request method GET:
    $.get(requestURL, function(responseJSON) {
        // before add country to dropDown -> clear content of dropdown in advance
        dropDownListCountryForStates.empty();

        $.each(responseJSON, function (index, item) {
            let optionValue = item.id;
            let optionName = item.name;

            $("<option>").val(optionValue).text(optionName).appendTo(dropDownListCountryForStates);
        })
    })
        .done(function() { // done will be called when send request Success (200 OK)
            // change text of button Load:
            btnLoadCountryListForStates.text("Refresh Country List");
            showToastMessageForStates("Load country list successfully");
        })
        .fail(function(error) {
            showToastMessageForStates("ERROR: Could not connect to Server");
        })
}

function showToastMessageForStates(message) {
    const toastMessage = $('#toastMessageForStates');
    const toastMessageText = $('#toastMessageTextForStates');

    // show toast message
    toastMessage.removeClass("d-none").addClass("show");

    // Tự động ẩn thông báo sau 5 giây (tùy chọn)
    setTimeout(() => {
        toastMessage.removeClass('show').addClass('d-none');
    }, 10000);

    toastMessageText.text(message)
}
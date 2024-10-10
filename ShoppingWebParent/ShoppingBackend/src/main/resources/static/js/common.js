// using jquery:
$(document).ready(function (){
    // Handle Logout:
    var formLogout = $('#logoutForm');
    $('#btnLogout').on('click', function (e) {
        e.preventDefault(); // Ngăn chặn load behavior khi click thẻ <a>
        formLogout.submit();
    })

    customizeTabs();
})

function customizeTabs() {
    // JS to enable link to tab:
    var url = document.location.toString();
    if(url.match('#')) {
        $('.nav-tabs button[data-bs-target="#' + url.split("#")[1] + '"]').tab('show');
    }

    $('.nav-tabs button').on('shown.bs.tab', function(e) {
        window.location.hash = e.target.hash;
    })
}
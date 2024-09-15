// using jquery:
$(document).ready(function (){
    // Handle Logout:
    var formLogout = $('#logoutForm');
    $('#btnLogout').on('click', function (e) {
        e.preventDefault(); // Ngăn chặn load behavior khi click thẻ <a>
        formLogout.submit();
    })
})

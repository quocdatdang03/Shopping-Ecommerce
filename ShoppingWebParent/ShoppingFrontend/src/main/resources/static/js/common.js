var btnLogout;
$(document).ready(function() {
    btnLogout = $('#btnLogout');
    btnLogout.click(function(e) {
        e.preventDefault();
        $('#logoutForm').submit();
    })
})
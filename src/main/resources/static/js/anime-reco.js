$(document).ready(function() {

});

function showErrorModal(message) {
    showModal('Error', message);
}

function showModal(title, message) {
    $('#modalWindowTitle').html(title);
    $('#modalWindowMessage').html(message);
    $('#errorModal').modal('show');
}
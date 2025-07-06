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

function getPagination(totalPages, currentPage, functionName) {
    var spanPagination = '';
    var lastPageTriggered = false;
    var firstPageTriggered = false;
    if (totalPages > 0) {
        spanPagination += '<nav aria-label="...">';
        spanPagination += ' <ul class="pagination">';
        if (currentPage == 0) {
            spanPagination += '  <li class="page-item disabled">';
            spanPagination += '   <span class="page-link">Previous</span>';
            spanPagination += '  </li>';
        } else {
            spanPagination += '  <li class="page-item">';
            spanPagination += '   <a class="page-link" href="#a" onclick="'+functionName+'('
                + (currentPage - 1) + ')">Previous</a>';
            spanPagination += '  </li>';
        }
        if (currentPage - 3 > 1) {
            spanPagination += getPaginationLink(currentPage, 1, functionName);
            spanPagination += '  <li class="page-item disabled">';
            spanPagination += '   <span class="page-link">...</span>';
            spanPagination += '  </li>';
        }
        for (var i = currentPage - 3; i < currentPage + 4; i++) {
            if (i <= totalPages) {
                if (i == totalPages) {
                    lastPageTriggered = true;
                }
                spanPagination += getPaginationLink(currentPage, i, functionName);
            }
        }
        if (!lastPageTriggered) {
            spanPagination += '  <li class="page-item disabled">';
            spanPagination += '   <span class="page-link">...</span>';
            spanPagination += '  </li>';
            spanPagination += getPaginationLink(currentPage, totalPages, functionName);
        }
        if (currentPage+1 == totalPages) {
            spanPagination += '  <li class="page-item disabled">';
            spanPagination += '   <span class="page-link">Next</span>';
            spanPagination += '  </li>';
        } else {
            spanPagination += '  <li class="page-item">';
            spanPagination += '   <a class="page-link" href="#" onclick="'+functionName+'('
                + (currentPage + 1) + ')">Next</a>';
            spanPagination += '  </li>';
        }
        spanPagination += ' </ul>';
        spanPagination += '</nav>';
    }
    return spanPagination;
}

function getPaginationLink(currentPage, displayPage, functionName) {
    if (displayPage <= 0) {
        return '';
    }
    if (currentPage + 1 == displayPage) {
        return '<li class="page-item active" aria-current="page"><span class="page-link">' + displayPage + '</span></li>';
    }
    return '<li class="page-item"><a class="page-link" href="#" onclick="'+functionName+'('+ (displayPage - 1) + '); ">' +
    displayPage + '</a></li>';
}

function getAnimeCards(animeList) {
    let html = "";
    for(let i = 0; i < animeList.length; i++) {
        let altTitleContent = "<p>";
        for (let y = 0; y < animeList[i].altTitles.length; y++) {
            altTitleContent += animeList[i].altTitles[y];
            if (y+1 < animeList[i].altTitles.length) {
                altTitleContent += "</br>";
            }
        }
        altTitleContent += "</p>";
        html += `
            <div class="col me-2 my-2">
                <div class="card" onclick="openAnimeDetails(`+animeList[i].id+`);">
                    <img class="card-img-top img-wrapper" src="` + animeList[i].mainMediumUrl + `" alt="` + animeList[i].title + `">
                    <div class="card-footer limit-text-card" data-toggle="tooltip"
                        data-bs-title="`+altTitleContent+`"
                        data-bs-html="true"
                        data-bs-custom-class="alt-titles-tooltip"
                        <p class="card-text">` + animeList[i].title + `</p>
                    </div>
                </div>
            </div>
        `;
    }
    return html;
}

function getWatchlist(index) {
    $.ajax({
        type: "GET",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/watchlist/" + index,
        dataType: 'json',
        contentType: 'application/json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                displayWatchlist(data);
            }
        }
    });
}

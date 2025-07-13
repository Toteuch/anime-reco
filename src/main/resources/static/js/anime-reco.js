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
    var _page_displayed = 10;
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

        let startPageWithDot = currentPage > 4;
        let endPageWithDot = currentPage < totalPages - 1 - 5;

        spanPagination += getPaginationLink(currentPage, 0, functionName);

        if (totalPages > _page_displayed) {
            if (startPageWithDot) {
                spanPagination += '  <li class="page-item disabled">';
                spanPagination += '   <span class="page-link">...</span>';
                spanPagination += '  </li>';
            }

            // TODO : pages
            let startPage = 0;
            let endPage = 0
            if (!startPageWithDot && endPageWithDot) {
                startPage = 1;
                endPage = startPage + 7;
            } else if (startPageWithDot && endPageWithDot) {
                startPage = currentPage - 2;
                endPage = startPage + 6;
            } else if (startPageWithDot && !endPageWithDot) {
                endPage = totalPages - 1;
                startPage = endPage - 7;
            }

            for (let i = startPage; i < endPage; i++) {
                spanPagination += getPaginationLink(currentPage, i, functionName);
            }

            if (endPageWithDot) {
                spanPagination += '  <li class="page-item disabled">';
                spanPagination += '   <span class="page-link">...</span>';
                spanPagination += '  </li>';
            }
            spanPagination += getPaginationLink(currentPage, totalPages - 1, functionName);
        } else {
            for (let i = 1; i < totalPages; i++) {
                spanPagination += getPaginationLink(currentPage, i, functionName);
            }
        }

        if (currentPage+1 == totalPages) {
            spanPagination += '  <li class="page-item disabled">';
            spanPagination += '   <span class="page-link">Next</span>';
            spanPagination += '  </li>';
        } else {
            spanPagination += '  <li class="page-item">';
            spanPagination += '   <a class="page-link" href="#a" onclick="'+functionName+'('
                + (currentPage + 1) + ')">Next</a>';
            spanPagination += '  </li>';
        }
        spanPagination += ' </ul>';
        spanPagination += '</nav>';
    }
    return spanPagination;
}

function getPaginationLink(currentPage, displayPage, functionName) {
    if (displayPage < 0) {
        return '';
    }
    if (currentPage == displayPage) {
        return '<li class="page-item active" aria-current="page"><span class="page-link">' + (displayPage + 1) +
        '</span></li>';
    }
    return '<li class="page-item"><a class="page-link" href="#a" onclick="'+functionName+'('+ displayPage + '); ">' +
    (displayPage + 1) + '</a></li>';
}

function getAnimeCards(animeList) {
    if (animeList.length == 0) return null;
    let html = "";
    for (let i = 0; i < animeList.length; i++) {
        let isNotification = animeList[i].animeId != undefined;
        let mainMediumUrl = "";
        let textContent = "";
        let tooltip = "";
        let onclickMethod = "";
        if (isNotification) {
            mainMediumUrl = animeList[i].mainMediumUrl;
            textContent = getNotificationCardBody(animeList[i]);
            tooltip = "<p>" + animeList[i].animeTitle + "</p>";
            onclickMethod = "openNotification("+animeList[i].id+", "+animeList[i].animeId+");";
        } else {
            mainMediumUrl = animeList[i].mainMediumUrl;
            textContent = animeList[i].title;
            tooltip = "<p>";
            for (let y = 0; y < animeList[i].altTitles.length; y++) {
                tooltip += animeList[i].altTitles[y];
                if (y+1 < animeList[i].altTitles.length) {
                    tooltip += "</br>";
                }
            }
            tooltip += "</p>";
            onclickMethod = "openAnimeDetails("+animeList[i].id+");";
        }
        html += getAnimeCard(mainMediumUrl, textContent, tooltip, onclickMethod);
    }
    return html;
}

function getAnimeCard(mainMediumUrl, textContent, tooltip, onclickMethod) {
    return `
        <div class="col me-2 my-2">
            <div class="card" onclick="`+onclickMethod+`">
                <img class="card-img-top img-wrapper" src="` + mainMediumUrl + `" alt="` + textContent + `">
                <div class="card-footer limit-text-card" data-toggle="tooltip"
                    data-bs-title="`+tooltip+`"
                    data-bs-html="true"
                    data-bs-custom-class="alt-titles-tooltip"
                    <p class="card-text">` + textContent + `</p>
                </div>
            </div>
        </div>
    `;
}

function getNotificationCardBody(notification) {
    let html = "";
    let createdAt = new Date(notification.createdAt).toLocaleDateString(undefined);
    switch (notification.type) {
        case "NEW_RELATED_ANIME":
            html += "NEW sequel";
            break;
        case "STATUS_CHANGED":
            html += "NEW status";
            break;
        case "START_DATE_CHANGED":
            html += "NEW date";
            break;
        default:
            html += " ";
    }
    html += "<br/>" + createdAt;
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

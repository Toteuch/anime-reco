function openAnimeDetails(animeId) {
    $.ajax({
        type: "GET",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/" + animeId,
        contentType: 'application/json',
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                showErrorModal("Your session has expired. Please refresh the page");
            } else {
                showErrorModal("Internal error");
            }
        }
    });
}

function setNotifications(animeId, enable) {
    $.ajax({
        type: "POST",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/" + animeId + "/notifications?enable=" +
        enable,
        contentType: 'application/json',
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                showErrorModal("Your session has expired. Please refresh the page");
            } else {
                showErrorModal("Internal error");
            }
        }
    });
}

function editWatchList(animeId, add) {
    let type = "DELETE";
    if (add) {
        type = "POST";
    }
    $.ajax({
        type: type,
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/"+animeId+"/watchlist",
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                showErrorModal("Your session has expired. Please refresh the page");
            } else {
                showErrorModal("Internal error");
            }
        }
    })
}

function getAnimeDetailsContent(animeDetails) {
    let html = `
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-body">
                    <div class="flex d-flex">
                        <div class="d-flex flex-column justify-content-center">
                            <img src="${animeDetails.mainPictureMediumUrl}" class="img-wrapper-xl mx-2"/>
                        </div>
                        <div class="flex flex-column">
                        <div>
                            <h2>${animeDetails.mainTitle}</h2>
                            <p><i>`;
    for (let i = 0; i < animeDetails.alternativeTitles.length; i++) {
        html += `${animeDetails.alternativeTitles[i]}`;
        if (i + 1 < animeDetails.alternativeTitles.length) {
            html += " | ";
        }
    }
    html += `               </i></p>
                            <p>${animeDetails.mediaTypeLabel} | ${animeDetails.numEpisodes} episodes |
                            ${animeDetails.statusLabel}`;
    if (animeDetails.seasonLabel != null) {
        html += " | ";
        html += animeDetails.seasonLabel;
    }
    if (animeDetails.startDate != null) {
        html += " | ";
        html += new Date(animeDetails.startDate).toLocaleDateString(undefined);
        if (animeDetails.endDate != null) {
            html += " - ";
            html += new Date(animeDetails.endDate).toLocaleDateString(undefined);
        }
    }
    html += "</p><p>";
    let elements = 0;
    if (animeDetails.animeScore != null) {
        html += '<i class="bi bi-globe"></i> ' + animeDetails.animeScore;
        elements++;
    }
    if (animeDetails.userScore != null) {
        if (elements > 0) {
            html += ' | ';
        }
        html += '<i class="bi bi-person-fill"></i> ' + animeDetails.userScore;
        elements++;
    }
    if (elements > 0) {
        html += ' | ';
    }
    html += '<a href="https://myanimelist.net/anime/'+animeDetails.id+'">MAL page</a>';
    if (animeDetails.lastUpdate != null) {
        html += ' | <i>last update : ' + new Date(animeDetails.lastUpdate).toLocaleDateString(undefined) + '</i>';
    }
    html += "</p>";
    if (animeDetails.synopsis != null) {
        html += "<p>" + animeDetails.synopsis + "</p>";
    }
    if (animeDetails.genreLabels != null && animeDetails.genreLabels.length > 0) {
        html += `<p>`;
        for (let i = 0; i < animeDetails.genreLabels.length; i++) {
            html += `<span class="badge rounded-pill text-bg-primary mx-1">${animeDetails.genreLabels[i]}</span>`;
        }
        html += `</p>`;
    }
    if (animeDetails.pictureLinks != null && animeDetails.pictureLinks.length > 0) {
        html += `<div class="flex d-flex flex-wrap">`;
            for (let i = 0; i < animeDetails.pictureLinks.length; i++) {
                html += `<img class="img-thumbnail thumbnail" src="${animeDetails.pictureLinks[i]}"/>`;
            }
        html += `</div>`;
    }
    html += `
                            </div>
                        </div>
                    </div>
                </div>
            <div class="modal-footer d-flex justify-content-between">`;
    if (animeDetails.prequelAnimeId != null) {
        html += `<div><button class="btn btn-primary" type="button"
        onclick="openAnimeDetails(${animeDetails.prequelAnimeId})">Previous</button></div>`;
    } else {
        html += `<div><button class="btn btn-outline-secondary" type="button" disabled>Previous</button></div>`;
    }
    html += `   <div>`;
    if (animeDetails.notificationsEnabled) {
        html += `<button class="btn btn-primary" type="button" onclick="setNotifications(${animeDetails.id}, false)">
                     <i class="bi bi-bell"></i>
                 </button>`;
    } else if ($('#isAuthenticated').val() == "true") {
        html += `<button class="btn btn-outline-primary" type="button" onclick="setNotifications(${animeDetails.id}, true)">
                     <i class="bi bi-bell-slash"></i>
                 </button>`;
    } else {
        html += `<button class="btn btn-outline-primary" type="button" disabled>
                    <i class="bi bi-bell-slash"></i>
                </button>`;
    }
    if (animeDetails.inWatchlist == true) {
        html += `<button class="btn btn-primary mx-2" type="button" onclick="editWatchList(${animeDetails.id}, false);">
                    <i class="bi bi-bookmark-check"></i>
                 </button>`;
    } else if (animeDetails.addableToWatchlist == true) {
        html += `
            <button class="btn btn-outline-primary mx-2" type="button"
                onclick="editWatchList(${animeDetails.id}, true);">
                <i class="bi bi-bookmark"></i>
            </button>`;
    } else {
        html += `
            <button class="btn btn-outline-primary mx-2" type="button" disabled>
                <i class="bi bi-bookmark"></i>
            </button>`;
    }
    if (animeDetails.excluded == true) {
        html += `
            <button class="btn btn-primary me-2" type="button"
                onclick="excludeRecommendation(${animeDetails.id}, false)">
                <i class="bi bi-ban"></i>
            </button>
        `;
    } else if (animeDetails.excludable == true) {
        html += `
            <button class="btn btn-outline-primary me-2" type="button"
                onclick="excludeRecommendation(${animeDetails.id}, true)">
                <i class="bi bi-ban"></i>
            </button>
        `;
    } else {
        html += `
            <button class="btn btn-outline-primary me-2" type="button" disabled>
                <i class="bi bi-ban"></i>
            </button>
        `;
    }
    html += `<button class="btn btn-secondary" data-bs-dismiss="modal" type="button">Close</button>
            </div>`;
    if (animeDetails.sequelAnimeId != null) {
        html += `<div><button class="btn btn-primary" type="button"
        onclick="openAnimeDetails(${animeDetails.sequelAnimeId})">Next</button></div>`;
    } else {
        html += `<div><button class="btn btn-outline-secondary" type="button" disabled>Next</button></div>`;
    }
    html += `</div>
        </div>
    `;
    return html;
}

function excludeRecommendation(animeId, exclude) {
    $.ajax({
        type: "PUT",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/" + animeId + "?exclude=" +
        exclude,
        contentType: 'application/json',
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                showErrorModal("Your session has expired. Please refresh the page");
            } else {
                showErrorModal("Internal error");
            }
        }
    });
}
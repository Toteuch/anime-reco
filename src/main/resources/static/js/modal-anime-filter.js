$(function() {
    displaySearchFilter(undefined);
});

function displaySearchFilter(searchFilter) {
    let html = `
        <div id="filterFirstRow" class="d-flex">
            <div class="flex-grow-1 p-2">
    `;
    if (searchFilter != undefined && searchFilter.title != undefined) {
        html += `<input class="form-control" id="filterTitleInput" placeholder="Title" value="${searchFilter.title}"
            type="text">`;
    } else {
        html += `<input class="form-control" id="filterTitleInput" placeholder="Title" type="text">`;
    }
    html += `
            </div>
            <div class="p-2">
    `;
    if ($('#isAuthenticated').val() == "true") {
        html += `<button class="btn btn-primary" type="button" onclick="openFilterNameModal();">`;
    } else {
        html += `<button class="btn btn-primary" type="button" disabled>`;
    }
    html += `
                    <i class="bi bi-floppy"></i>
                </button>
            </div>
            <div class="p-2">
    `;
    if ($('#isAuthenticated').val() == "true") {
        html += `<button class="btn btn-primary" type="button" onclick="openSearchFilters()">`;
    } else {
        html += `<button class="btn btn-primary" type="button" disabled>`;
    }

    html += `
                    <i class="bi bi-folder2-open"></i>
                </button>
            </div>
        </div>
        <div class="p-2" id="mediaTypesFormInput"></div>
        <div class="d-flex p-2">
            <div id="seasonFormInputs" class="d-flex">
                <div class="input-group">
                    <span class="input-group-text">From</span>
    `;
    if (searchFilter == undefined || searchFilter.minSeasonYear == undefined) {
        html += `<input class="form-control w-5" id="minSeasonInput" type="number"/>`;
    } else {
        html += `<input class="form-control w-5" id="minSeasonInput" type="number" value="${searchFilter
            .minSeasonYear}"/>`;
    }
    html += `
                    <span class="input-group-text">To</span>
    `;
    if (searchFilter == undefined || searchFilter.maxSeasonYear == undefined) {
        html += `<input class="form-control w-5" id="maxSeasonInput" type="number"/>`;
    } else {
        html += `<input class="form-control w-5" id="maxSeasonInput" type="number" value="${searchFilter
            .maxSeasonYear}"/>`;
    }
    html += `
                </div>
            </div>
            <div id="statusFormInput"></div>
        </div>
        <div class="p-2" id="genresFormInput"></div>
    `;

    $('#modalBodyFilter').html(html);
    var responseMediaTypes = displayMediaTypesFilter(searchFilter);
    var responseStatus = displayStatusFilter(searchFilter);
    displayGenresFilter(searchFilter);
    // after both ajax calls has been executed
    $.when(responseMediaTypes, responseStatus).then(function() {
        activate2States();
    });
    if (searchFilter != undefined) {
        $("#filterId").val(searchFilter.id);
        $("#filterNameInput").val(searchFilter.name);
        $("#updateFilterButton").prop({disabled: false});
    }
}

function openFilterNameModal() {
    $("#filterNameModal").modal('show');
}

function cancelFilterNameModal() {
    $("#filterNameModal").modal('hide');
}

function updateSearchFilter() {
    let id = $("#filterId").val();
    saveSearchFilter(id);
}

function saveSearchFilter(id) {
    let filter = getSearchFilter(undefined, id);
    $.ajax({
        type: "POST",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/search-filter",
        data: JSON.stringify(filter),
        dataType: 'json',
        contentType: 'application/json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                $("#filterId").val(data.searchFilter.id);
                $("#updateFilterButton").prop({disabled: false});
                $("#filterNameModal").modal('hide');
                $("#filterModal").modal('show');
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

function activate2States() {
    $('button[type="button"][class~="btn-filter-2"]').on('click', function() {
         var colorClasses = ["btn-outline-primary", "btn-primary"];
         var $radios = $('input[type="radio"][name="' + $(this).attr('id') + '"]');
         var $checked = $radios.filter(':checked');
         var $next = $radios.eq($radios.index($checked) + 1);
         if (!$next.length) {
             $next = $radios.first();
         }
         $next.prop("checked", true);
         var newValue = $radios.filter(':checked').val();
         $(this)
             .removeClass(colorClasses.join(" "))
             .addClass(colorClasses[newValue]);
     });
 }

 function activate3States() {
     $('button[type="button"][class~="btn-filter-3"]').on('click', function() {
         var colorClasses = ["btn-outline-primary", "btn-primary", "btn-danger"];
         var $radios = $('input[type="radio"][name="' + $(this).attr('id') + '"]');
         var $checked = $radios.filter(':checked');
         var $next = $radios.eq($radios.index($checked) + 1);
         if (!$next.length) {
             $next = $radios.first();
         }
         $next.prop("checked", true);
         var newValue = $radios.filter(':checked').val();
         $(this)
             .removeClass(colorClasses.join(" "))
             .addClass(colorClasses[newValue]);
     });
 }

 function displayGenresFilter(searchFilter) {
     $.ajax({
         type: "GET",
         headers: {"X-CSRF-Token": $('#csrf-token').val()},
         url: window.location.protocol + "//" + window.location.host + "/search-filter/genre",
         dataType: 'json',
         success: function(data) {
             var genresMap = new Map(Object.entries(data));
             let hiddenInputs = "";
             let buttonGroup = "";
             for(const [key, value] of genresMap) {
                if (searchFilter != undefined && searchFilter.genres != undefined && searchFilter.genres.includes
                (parseInt(key)))
                {
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="0"/>`;
                    hiddenInputs += `<input checked hidden name="genre_`+key+`" type="radio" value="1"/>`;
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="2"/>`;
                    buttonGroup += `<button class="btn btn-filter-3 btn-primary mb-1" id="genre_`+key+`" type="button">`+value+`</button>`;
                } else if (searchFilter != undefined && searchFilter.negativeGenres != undefined && searchFilter
                .negativeGenres.includes(parseInt(key))) {
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="0"/>`;
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="1"/>`;
                    hiddenInputs += `<input checked hidden name="genre_`+key+`" type="radio" value="2"/>`;
                    buttonGroup += `<button class="btn btn-filter-3 btn-danger mb-1" id="genre_`+key+`"
                    type="button">`+value+`</button>`;
                } else {
                    hiddenInputs += `<input checked hidden name="genre_`+key+`" type="radio" value="0"/>`;
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="1"/>`;
                    hiddenInputs += `<input hidden name="genre_`+key+`" type="radio" value="2"/>`;
                    buttonGroup += `<button class="btn btn-filter-3 btn-outline-primary mb-1" id="genre_`+key+`" type="button">`+value+`</button>`;
                }
             }
             let html = hiddenInputs;
             html += '<label for="genresBtnGroup">Genres</label>';
             html += '<div class="btn-group d-flex flex-wrap" id="genresBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#genresFormInput').html(html);
             activate3States();
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

 function displayStatusFilter(searchFilter) {
     var response = $.ajax({
         type: "GET",
         headers: {"X-CSRF-Token": $('#csrf-token').val()},
         url: window.location.protocol + "//" + window.location.host + "/search-filter/status",
         dataType: 'json',
         success: function(data) {
             var statusMap = new Map(Object.entries(data));
             let hiddenInputs = "";
             let buttonGroup = "";
             for(const [key, value] of statusMap) {
                if (searchFilter != undefined && searchFilter.statusList != undefined && searchFilter.statusList
                    .includes(key)) {
                    hiddenInputs += `<input hidden name="status_`+key+`" type="radio" value="0">`;
                    hiddenInputs += `<input checked hidden name="status_`+key+`" type="radio" value="1">`;
                    buttonGroup += `<button class="btn btn-filter-2 btn-primary" id="status_`+key+`"
                        type="button">`+value+`</button>`;
                } else {
                    hiddenInputs += `<input checked hidden name="status_`+key+`" type="radio" value="0">`;
                    hiddenInputs += `<input hidden name="status_`+key+`" type="radio" value="1">`;
                    buttonGroup += `<button class="btn btn-filter-2 btn-outline-primary" id="status_`+key+`"
                        type="button">`+value+`</button>`;
                }
             }
             let html = hiddenInputs;
             html += '<label for="statusBtnGroup" class="ps-2">Status</label>';
             html += '<div class="btn-group px-2" id="statusBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#statusFormInput').html(html);
         },
         error: function(xhr, ajaxOptions, thrownError) {
             if (xhr.status == 403) {
                 showErrorModal("Your session has expired. Please refresh the page");
             } else {
                 showErrorModal("Internal error");
             }
         }
     });
     return response;
 }

 function displayMediaTypesFilter(searchFilter) {
     var response = $.ajax({
         type: "GET",
         headers: {"X-CSRF-Token": $('#csrf-token').val()},
         url: window.location.protocol + "//" + window.location.host + "/search-filter/media-type",
         dataType: 'json',
         success: function(data) {
             var mediaTypes = new Map(Object.entries(data));
             let hiddenInputs = "";
             let buttonGroup = "";
             for(const [key, value] of mediaTypes) {
                if (searchFilter != undefined && searchFilter.mediaTypes != undefined && searchFilter.mediaTypes
                .includes(key)) {
                    hiddenInputs += `<input hidden name="mediaType_`+key+`" type="radio" value="0">`;
                    hiddenInputs += `<input checked hidden name="mediaType_`+key+`" type="radio" value="1">`;
                    buttonGroup += `<button class="btn btn-filter-2 btn-primary" id="mediaType_`+key+`"
                                     type="button">`+value+`</button>`;
                } else {
                    hiddenInputs += `<input checked hidden name="mediaType_`+key+`" type="radio" value="0">`;
                    hiddenInputs += `<input hidden name="mediaType_`+key+`" type="radio" value="1">`;
                    buttonGroup += `<button class="btn btn-filter-2 btn-outline-primary" id="mediaType_`+key+`"
                                     type="button">`+value+`</button>`;
                }

             }
             let html = hiddenInputs;
             html += '<label for="mediaTypeBtnGroup">Media Source</label>';
             html += '<div class="btn-group p-2" id="mediaTypeBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#mediaTypesFormInput').html(html);
         },
         error: function(xhr, ajaxOptions, thrownError) {
             if (xhr.status == 403) {
                 showErrorModal("Your session has expired. Please refresh the page");
             } else {
                 showErrorModal("Internal error");
             }
         }
     });
     return response;
 }

 function getSearchFilter(pageNumber, id) {
    var name = $('#filterNameInput').val();
    var animeTitle = $('#filterTitleInput').val();
    if (animeTitle != null) {
        animeTitle = animeTitle.trim();
    }
    var selectedMinSeasonYear = $('#minSeasonInput').val() != '' ? parseInt($('#minSeasonInput').val()) : null;
    var selectedMaxSeasonYear = $('#maxSeasonInput').val() != '' ? parseInt($('#maxSeasonInput').val()) : null;
    var selectedMediaTypes = [];
    $('input[type="radio"][name^="mediaType_"]').filter(':checked').each(function() {
        if ($(this).val() == 1) {
            selectedMediaTypes.push($(this).attr('name').replace('mediaType_', ''));
        }
    });
    var selectedStatus = [];
    $('input[type="radio"][name^="status_"]').filter(':checked').each(function () {
        if ($(this).val() == 1) {
            selectedStatus.push($(this).attr('name').replace('status_', ''));
        }
    });
    var genresIn = [];
    var genresOut = [];
    $('input[type="radio"][name^="genre_"]').filter(':checked').each(function () {
        if ($(this).val() == 1) {
            genresIn.push(parseInt($(this).attr('name').replace('genre_', '')));
        } else if ($(this).val() == 2) {
            genresOut.push(parseInt($(this).attr('name').replace('genre_', '')));
        }
    });
    return {
        id: id,
        name: name,
        title: animeTitle,
        minSeasonYear: selectedMinSeasonYear,
        maxSeasonYear: selectedMaxSeasonYear,
        mediaTypes: selectedMediaTypes,
        statusList: selectedStatus,
        genres: genresIn,
        negativeGenres: genresOut,
        pageNumber: pageNumber
    };
 }

 function closeOpenSearchFilters() {
     $('#searchFilterListModal').modal('hide');
 }

 function openSearchFilter(searchFilterId) {
     $.ajax({
         type: "GET",
         headers: {"X-CSRF-Token": $('#csrf-token').val()},
         url: window.location.protocol + "//" + window.location.host + "/search-filter/" + searchFilterId,
         success: function(data) {
             if (data.error != null) {
                 showErrorModal(data.error);
             } else {
                 displaySearchFilter(data.searchFilter);
                 $('#searchFilterListModal').modal('hide');
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

 function openSearchFilters() {
     $.ajax({
         type: "GET",
         headers: {"X-CSRF-Token": $('#csrf-token').val()},
         url: window.location.protocol + "//" + window.location.host + "/search-filter",
         success: function(data) {
             if (data.error != null) {
                 showErrorModal(data.error);
             } else {
                 if (data.searchFilters.length == 0) {
                     showErrorModal("No filter saved.");
                 } else {
                     displaySearchFilters(data.searchFilters);
                 }
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

 function displaySearchFilters(searchFilters) {
     let content = "";
     for (let i = 0; i < searchFilters.length; i++) {
         content += `
             <div class="flex d-flex">
                 <p>${searchFilters[i].name}<p>
                 <a href="#" onclick="openSearchFilter(${searchFilters[i].id})">
                     <i class="bi bi-arrow-right-short"></i>
                 </a>
             </div>
         `;
     }
     $('#searchFilterListModalBody').html(content);
     $('#searchFilterListModal').modal('show');
 }
$(function() {
    let html = '<div id="filterFirstRow" class="d-flex">';
    html += '       <div class="flex-grow-1 p-2">';
    html += '           <input class="form-control" id="filterTitleInput" placeholder="Title" type="text">';
    html += '       </div>';
    html += '       <div class="p-2">';
    html += '           <button class="btn btn-primary" type="button">';
    html += '               <svg class="bi bi-floppy" fill="currentColor" height="16" viewBox="0 0 16 16" width="16" xmlns="http://www.w3.org/2000/svg">';
    html += '                   <path d="M11 2H9v3h2z"/>';
    html += '                   <path d="M1.5 0h11.586a1.5 1.5 0 0 1 1.06.44l1.415 1.414A1.5 1.5 0 0 1 16 2.914V14.5a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 14.5v-13A1.5 1.5 0 0 1 1.5 0M1 1.5v13a.5.5 0 0 0 .5.5H2v-4.5A1.5 1.5 0 0 1 3.5 9h9a1.5 1.5 0 0 1 1.5 1.5V15h.5a.5.5 0 0 0 .5-.5V2.914a.5.5 0 0 0-.146-.353l-1.415-1.415A.5.5 0 0 0 13.086 1H13v4.5A1.5 1.5 0 0 1 11.5 7h-7A1.5 1.5 0 0 1 3 5.5V1H1.5a.5.5 0 0 0-.5.5m3 4a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 .5-.5V1H4zM3 15h10v-4.5a.5.5 0 0 0-.5-.5h-9a.5.5 0 0 0-.5.5z"/>';
    html += '               </svg>';
    html += '           </button>';
    html += '       </div>';
    html += '       <div class="p-2">';
    html += '           <button class="btn btn-primary" type="button">';
    html += '               <svg class="bi bi-folder2" fill="currentColor" height="16" viewBox="0 0 16 16" width="16" xmlns="http://www.w3.org/2000/svg">';
    html += '                   <path d="M1 3.5A1.5 1.5 0 0 1 2.5 2h2.764c.958 0 1.76.56 2.311 1.184C7.985 3.648 8.48 4 9 4h4.5A1.5 1.5 0 0 1 15 5.5v7a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 1 12.5zM2.5 3a.5.5 0 0 0-.5.5V6h12v-.5a.5.5 0 0 0-.5-.5H9c-.964 0-1.71-.629-2.174-1.154C6.374 3.334 5.82 3 5.264 3zM14 7H2v5.5a.5.5 0 0 0 .5.5h11a.5.5 0 0 0 .5-.5z"/>';
    html += '               </svg>';
    html += '           </button>';
    html += '       </div>';
    html += '   </div>';
    html += '   <div class="p-2" id="mediaTypesFormInput">';
    html += '   </div>';
    html += '   <div class="d-flex p-2">';
    html += '       <div id="seasonFormInputs" class="d-flex">';
    html += '           <div class="input-group">';
    html += '               <span class="input-group-text">From</span>';
    html += '               <input class="form-control w-5" id="minSeasonInput" type="number"/>';
    html += '               <span class="input-group-text">To</span>';
    html += '               <input class="form-control w-5" id="maxSeasonInput" type="number"/>';
    html += '           </div>';
    html += '       </div>';
    html += '       <div id="statusFormInput">';
    html += '       </div>';
    html += '   </div>';
    html += '   <div class="p-2" id="genresFormInput">';
    html += '   </div>';

    $('#modalBodyFilter').html(html);
    var responseMediaTypes = displayMediaTypesFilter();
    var responseStatus = displayStatusFilter();
    displayGenresFilter();
    // after both ajax calls has been executed
    $.when(responseMediaTypes, responseStatus).then(function() {
        activate2States();
    });
});

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

 function displayGenresFilter() {
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
                 hiddenInputs += '<input checked hidden name="genre_'+key+'" type="radio" value="0"/>';
                 hiddenInputs += '<input hidden name="genre_'+key+'" type="radio" value="1"/>';
                 hiddenInputs += '<input hidden name="genre_'+key+'" type="radio" value="2"/>';
                 buttonGroup += '<button class="btn btn-filter-3 btn-outline-primary mb-1" id="genre_'+key+'" type="button">'+value+'</button>';
             }
             let html = hiddenInputs;
             html += '<label for="genresBtnGroup">Genres</label>';
             html += '<div class="btn-group d-flex flex-wrap" id="genresBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#genresFormInput').html(html);
             activate3States();
         }
     });
 }

 function displayStatusFilter() {
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
                 hiddenInputs += '<input checked hidden name="status_'+key+'" type="radio" value="0">';
                 hiddenInputs += '<input hidden name="status_'+key+'" type="radio" value="1">';
                 buttonGroup += '<button class="btn btn-filter-2 btn-outline-primary" id="status_'+key+'" type="button">'+value+'</button>'
             }
             let html = hiddenInputs;
             html += '<label for="statusBtnGroup" class="ps-2">Status</label>';
             html += '<div class="btn-group px-2" id="statusBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#statusFormInput').html(html);
         }
     });
     return response;
 }

 function displayMediaTypesFilter() {
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
                 hiddenInputs += '<input checked hidden name="mediaType_'+key+'" type="radio" value="0">';
                 hiddenInputs += '<input hidden name="mediaType_'+key+'" type="radio" value="1">';
                 buttonGroup += '<button class="btn btn-filter-2 btn-outline-primary" id="mediaType_'+key+'" type="button">'+value+'</button>'
             }
             let html = hiddenInputs;
             html += '<label for="mediaTypeBtnGroup">Media Source</label>';
             html += '<div class="btn-group p-2" id="mediaTypeBtnGroup" role="group">';
             html += buttonGroup;
             html += '</div>';
             $('#mediaTypesFormInput').html(html);
         }
     });
     return response;
 }

 function getSearchFilter(pageNumber) {
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
        title: animeTitle,
        minSeasonYear: selectedMinSeasonYear,
        maxSeasonYear: selectedMaxSeasonYear,
        mediaTypes: selectedMediaTypes,
        status: selectedStatus,
        genres: genresIn,
        negativeGenres: genresOut,
        pageNumber: pageNumber
    };
 }
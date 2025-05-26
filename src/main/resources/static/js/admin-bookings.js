// Функция для фильтрации бронирований
function filterBookings() {
    const searchText = document.getElementById('searchInput').value.toUpperCase();
    const statusFilter = document.getElementById('statusFilter').value;
    const placeTypeFilter = document.getElementById('placeTypeFilter').value.toLowerCase();

    const rows = document.getElementsByClassName('booking-row');
    let visibleCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        let matchesSearch = false;
        let matchesStatus = statusFilter === 'ALL' || row.getAttribute('data-status') === statusFilter;
        let matchesPlaceType = placeTypeFilter === 'all' ||
            row.getAttribute('data-place-type').includes(placeTypeFilter);

        // Поиск по тексту
        if (searchText) {
            for (let j = 0; j < row.cells.length - 1; j++) {
                const cell = row.cells[j];
                if (cell) {
                    const text = cell.textContent || cell.innerText;
                    if (text.toUpperCase().includes(searchText)) {
                        matchesSearch = true;
                        break;
                    }
                }
            }
        } else {
            matchesSearch = true;
        }

        // Показываем строку если все условия выполнены
        const shouldShow = matchesSearch && matchesStatus && matchesPlaceType;
        row.style.display = shouldShow ? "" : "none";

        if (shouldShow) visibleCount++;
    }

    // Показываем сообщение если ничего не найдено
    document.getElementById('noResults').style.display = visibleCount > 0 ? "none" : "block";
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    filterBookings();

    // Восстанавливаем параметры из URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('search')) {
        document.getElementById('searchInput').value = urlParams.get('search');
    }
    if (urlParams.has('status')) {
        document.getElementById('statusFilter').value = urlParams.get('status');
    }
    if (urlParams.has('placeType')) {
        document.getElementById('placeTypeFilter').value = urlParams.get('placeType');
    }
});
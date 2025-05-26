// Функция для фильтрации бронирований
function filterBookings() {
    const searchText = document.getElementById('searchInput').value.toUpperCase();
    const statusFilter = document.getElementById('statusFilter').value;
    const placeTypeFilter = document.getElementById('placeTypeFilter').value.toLowerCase();

    const table = document.getElementById('bookingsTable');
    const rows = table.querySelectorAll('tbody tr.booking-row');
    let visibleCount = 0;

    rows.forEach(row => {
        let matchesSearch = false;
        let matchesStatus = statusFilter === 'ALL' || row.getAttribute('data-status') === statusFilter;
        let matchesPlaceType = placeTypeFilter === 'all' ||
            row.getAttribute('data-place-type').includes(placeTypeFilter);

        // Поиск по тексту во всех видимых ячейках
        if (searchText) {
            const cells = row.querySelectorAll('td');
            cells.forEach(cell => {
                const text = cell.textContent || cell.innerText;
                if (text.toUpperCase().includes(searchText)) {
                    matchesSearch = true;
                }
            });
        } else {
            matchesSearch = true;
        }

        // Показываем строку если все условия выполнены
        const shouldShow = matchesSearch && matchesStatus && matchesPlaceType;
        row.style.display = shouldShow ? "" : "none";

        if (shouldShow) visibleCount++;
    });

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
        filterBookings();
    }
    if (urlParams.has('status')) {
        document.getElementById('statusFilter').value = urlParams.get('status');
        filterBookings();
    }
    if (urlParams.has('placeType')) {
        document.getElementById('placeTypeFilter').value = urlParams.get('placeType');
        filterBookings();
    }
});

// Делаем функцию доступной глобально, если она вызывается из HTML
window.filterBookings = filterBookings;
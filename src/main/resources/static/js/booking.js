// Все возможные часы для бронирования
// Константы и настройки
const ALL_HOURS = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00',
    '15:00', '16:00', '17:00', '18:00', '19:00', '20:00',
    '21:00', '22:00'];
const PLACE_TYPES = {
    WORKSPACE: 'рабочее место',
    MEETING_ROOM: 'переговорная'
};
const PLACES_COUNT = {
    [PLACE_TYPES.WORKSPACE]: 25,  // 25 рабочих мест
    [PLACE_TYPES.MEETING_ROOM]: 3 // 3 переговорные
};

// DOM элементы
const bookingDate = document.getElementById('bookingDate');
const startTime1 = document.getElementById('startTime');
const endTime1 = document.getElementById('endTime');
const bookBtn1 = document.getElementById('bookBtn1');
const startTime2 = document.getElementById('startTime2');
const endTime2 = document.getElementById('endTime2');
const bookBtn2 = document.getElementById('bookBtn2');
const successModal = document.getElementById('successModal');
const modalMessage = document.getElementById('modalMessage');
const modalCloseBtn = document.getElementById('modalCloseBtn');

// Установка минимальной даты (сегодня)
const today = new Date().toISOString().split('T')[0];
bookingDate.min = today;
bookingDate.value = today;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', async function() {
    await initTimeSelectors();
    setupEventListeners();
});

// Основные функции

async function initTimeSelectors() {
    const date = bookingDate.value;
    const [workspaceHours, meetingRoomHours] = await Promise.all([
        getAvailableHours(date, PLACE_TYPES.WORKSPACE),
        getAvailableHours(date, PLACE_TYPES.MEETING_ROOM)
    ]);

    fillTimeSelect(startTime1, workspaceHours);
    fillTimeSelect(startTime2, meetingRoomHours);
    hideAllBookButtons();
}

function setupEventListeners() {
    // Обработчики для модального окна
    modalCloseBtn.addEventListener('click', () => successModal.classList.remove('active'));
    successModal.addEventListener('click', (e) => {
        if (e.target === successModal) successModal.classList.remove('active');
    });

    // Обработчик изменения даты
    bookingDate.addEventListener('change', async function() {
        await initTimeSelectors();
    });

    // Обработчики для рабочих мест
    startTime1.addEventListener('change', function() {
        updateEndTimeOptions(this, endTime1, bookBtn1);
    });
    endTime1.addEventListener('change', function() {
        updateBookingButton(this, startTime1, bookBtn1);
    });

    // Обработчики для переговорных
    startTime2.addEventListener('change', function() {
        updateEndTimeOptions(this, endTime2, bookBtn2);
    });
    endTime2.addEventListener('change', function() {
        updateBookingButton(this, startTime2, bookBtn2);
    });

    // Кнопки бронирования
    bookBtn1.addEventListener('click', () => handleBooking(PLACE_TYPES.WORKSPACE, startTime1, endTime1));
    bookBtn2.addEventListener('click', () => handleBooking(PLACE_TYPES.MEETING_ROOM, startTime2, endTime2));
}

// Функции работы с временем

async function getAvailableHours(date, placeType) {
    try {
        const bookedSlots = await loadBookedSlots(date);
        const currentHour = getCurrentHour();
        const isToday = date === today;

        return ALL_HOURS.filter(hour => {
            if (isToday && hour < currentHour) return false;

            const hourNum = parseInt(hour.split(':')[0]);
            const nextHour = `${(hourNum + 1).toString().padStart(2, '0')}:00`;

            return isTimeAvailable(bookedSlots, placeType, hour, nextHour);
        });
    } catch (error) {
        console.error('Ошибка получения доступных часов:', error);
        return [];
    }
}

function updateEndTimeOptions(startSelect, endSelect, bookBtn) {
    if (startSelect.value) {
        endSelect.disabled = false;
        endSelect.innerHTML = '<option value="">-- Выберите --</option>';

        const startHour = parseInt(startSelect.value.split(':')[0]);
        const availableEndHours = ALL_HOURS
            .filter(hour => parseInt(hour.split(':')[0]) > startHour);

        availableEndHours.forEach(hour => {
            endSelect.innerHTML += `<option value="${hour}">${hour}</option>`;
        });
    } else {
        endSelect.disabled = true;
        endSelect.innerHTML = '<option value="">-- Выберите --</option>';
        bookBtn.style.display = 'none';
    }
}

function updateBookingButton(endSelect, startSelect, bookBtn) {
    if (endSelect.value && startSelect.value) {
        bookBtn.style.display = 'block';
    } else {
        bookBtn.style.display = 'none';
    }
}

// Функции работы с бронированиями

async function loadBookedSlots(date) {
    try {
        const response = await fetch(`/api/bookings/slots?date=${date}`);
        if (!response.ok) throw new Error('Ошибка загрузки данных');
        return await response.json();
    } catch (error) {
        console.error('Ошибка загрузки броней:', error);
        return [];
    }
}

function isTimeAvailable(bookedSlots, placeType, startTime, endTime) {
    const start = parseInt(startTime.split(':')[0]);
    const end = parseInt(endTime.split(':')[0]);

    let bookedCount = 0;

    bookedSlots.forEach(slot => {
        if (slot.placeType !== placeType) return;

        const slotStart = parseInt(slot.startTime.split(':')[0]);
        const slotEnd = parseInt(slot.endTime.split(':')[0]);

        if ((start < slotEnd) && (end > slotStart)) {
            bookedCount++;
        }
    });

    return bookedCount < PLACES_COUNT[placeType];
}

async function handleBooking(placeType, startSelect, endSelect) {
    if (!startSelect.value || !endSelect.value) return;

    try {
        // 1. Проверка доступности
        const checkResponse = await fetch('/api/bookings/check', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                date: bookingDate.value,
                startTime: startSelect.value,
                endTime: endSelect.value,
                placeType: placeType
            })
        });

        const checkResult = await checkResponse.json();
        if (!checkResult.available) {
            throw new Error(checkResult.message || 'Место недоступно');
        }

        // 2. Создание брони
        const bookResponse = await fetch('/api/bookings', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                date: bookingDate.value,
                startTime: startSelect.value,
                endTime: endSelect.value,
                placeType: placeType
            })
        });

        if (!bookResponse.ok) {
            throw new Error('Ошибка сервера при бронировании');
        }

        // 3. Обновление интерфейса
        await updateAvailableSlots();
        showSuccessMessage('Бронирование успешно создано!');
    } catch (error) {
        console.error("Ошибка бронирования:", error);
        showSuccessMessage("Ошибка: " + error.message);
    }
}

// Функция обновления слотов
async function updateAvailableSlots() {
    const date = bookingDate.value;
    const [workspaceSlots, meetingSlots] = await Promise.all([
        fetch(`/api/bookings/slots?date=${date}&placeType=рабочее%20место`),
        fetch(`/api/bookings/slots?date=${date}&placeType=переговорная`)
    ]);

    const workspaceBookings = await workspaceSlots.json();
    const meetingBookings = await meetingSlots.json();

    // Обновляем доступные часы для рабочих мест
    const workspaceHours = ALL_HOURS.filter(hour => {
        const hourStart = parseInt(hour.split(':')[0]);
        const hourEnd = hourStart + 1;
        const overlapping = workspaceBookings.filter(b =>
            hourStart < b.endTime && hourEnd > b.startTime
        ).length;
        return overlapping < 25;
    });

    // Обновляем доступные часы для переговорных
    const meetingHours = ALL_HOURS.filter(hour => {
        const hourStart = parseInt(hour.split(':')[0]);
        const hourEnd = hourStart + 1;
        const overlapping = meetingBookings.filter(b =>
            hourStart < b.endTime && hourEnd > b.startTime
        ).length;
        return overlapping < 3;
    });

    fillTimeSelect(startTime1, workspaceHours);
    fillTimeSelect(startTime2, meetingHours);
    hideAllBookButtons();
}

// Пример для Express.js
app.post('/api/bookings/check', async (req, res) => {
    const { date, startTime, endTime, placeType } = req.body;

    // 1. Получаем все брони для этого типа места и даты
    const bookings = await Booking.find({
        date,
        placeType,
        $or: [
            { startTime: { $lt: endTime }, endTime: { $gt: startTime } }
        ]
    });

    // 2. Проверяем количество
    const maxCount = placeType === 'WORKSPACE' ? 25 : 3;
    if (bookings.length >= maxCount) {
        return res.status(400).json({
            message: 'Все места этого типа уже заняты на выбранное время'
        });
    }

    res.status(200).json({ available: true });
});

app.post('/api/bookings', async (req, res) => {
    const session = await Booking.startSession();
    session.startTransaction();

    try {
        // Повторно проверяем доступность в транзакции
        const existing = await Booking.countDocuments({
            date: req.body.date,
            placeType: req.body.placeType,
            $or: [
                { startTime: { $lt: req.body.endTime }, endTime: { $gt: req.body.startTime } }
            ]
        }).session(session);

        const maxCount = req.body.placeType === 'WORKSPACE' ? 25 : 3;
        if (existing >= maxCount) {
            throw new Error('Места уже заняты');
        }

        // Создаем бронь
        const booking = new Booking(req.body);
        await booking.save({ session });

        await session.commitTransaction();
        res.status(201).json(booking);
    } catch (error) {
        await session.abortTransaction();
        res.status(400).json({ message: error.message });
    } finally {
        session.endSession();
    }
});

// Вспомогательные функции

function fillTimeSelect(selectElement, availableHours) {
    selectElement.innerHTML = '<option value="">-- Выберите --</option>';
    availableHours.forEach(hour => {
        selectElement.innerHTML += `<option value="${hour}">${hour}</option>`;
    });
}

function hideAllBookButtons() {
    bookBtn1.style.display = 'none';
    bookBtn2.style.display = 'none';
}

function getCurrentHour() {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    return `${hours}:00`;
}

function showSuccessMessage(message) {
    modalMessage.textContent = message;
    successModal.classList.add('active');
    setTimeout(() => successModal.classList.remove('active'), 3000);
}
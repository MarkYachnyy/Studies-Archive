DivFriends = $(".div__in__friends")[0];
DivIncomingRequests = $(".div__incoming__requests")[0];
DivSentRequests = $(".div__sent__requests")[0];
TextNewFriendRequestStatus = $(".text__new__request__status")[0];
ButtonSendNewRequest = $(".button__send__friend__request")[0];
InputNewFriendName = $(".input__add__friend__name")[0];
TextIncomingRequestsCount = $(".text__incoming__requests__count")[0];
TextSentRequestsCount = $(".text__sent__requests__count")[0];
TextFriendsCount = $(".text__friends__count")[0];
TextFriendsCurrentPage = $(".text__friends__current__page")[0];
ButtonFriendsPreviousPage = $(".button__friends__previous__page")[0];
ButtonFriendsNextPage = $(".button__friends__next__page")[0];
ItemsOnPage = 5;
CurrentPage = 1;
PageCount = 0;

ButtonFriendsNextPage.addEventListener("click", () => {
    if (CurrentPage < PageCount) {
        CurrentPage++;
        getFriendsList();
    }
});

ButtonFriendsPreviousPage.addEventListener("click", () => {
    if (CurrentPage > 1) {
        CurrentPage--;
        getFriendsList();
    }
});

function getFriendsList() {
    $.getJSON(`api/friends/part/${ItemsOnPage * (CurrentPage - 1)}-${ItemsOnPage * (CurrentPage)}`, null, data => {
        PageCount = Math.ceil(data.count/ItemsOnPage);
        TextFriendsCount.innerText = data.count;
        if(PageCount === 0){
            PageCount++;
        }
        TextFriendsCurrentPage.innerText = `Страница ${CurrentPage} из ${PageCount}`;
        setFriendsListHTML(data.content);
    });
}

function getIncomingRequsts() {
    $.getJSON("api/friends/all-requests-received", null, data => setIncomingRequestsListHTML(data));
}

function getSentRequsts() {
    $.getJSON("api/friends/all-requests-sent", null, data => setSentRequestsListHTML(data));
}

function setFriendsListHTML(friends_list) {
    let friend_count = 0;
    if (friends_list.length > 0) {
        DivFriends.innerHTML = "";
    } else {
        DivFriends.innerHTML = "<p style='color: gray'>Пользователи в друзьях отсутвствуют</p>";
    }
    for (let friend of friends_list) {
        friend_count++;
        let p = document.createElement("p");
        p.style.margin = "5px";
        p.style.padding = "10px";
        p.style.background = "#EEEEEE";

        p.appendChild(document.createTextNode(friend.name + " | "));

        let span = document.createElement("span");
        span.style.color = "green";
        span.innerText = `в друзьях с ${friend.date}`;
        p.appendChild(span);

        DivFriends.appendChild(p);
    }
}

function setIncomingRequestsListHTML(request_list) {
    TextIncomingRequestsCount.innerText = "0";
    let request_count = 0;
    if (request_list.length > 0) {
        DivIncomingRequests.innerHTML = "";
    } else {
        DivIncomingRequests.innerHTML = "<p style='color: gray'>Входящих запросов нет</p>";
    }
    for (let i = 0; i < request_list.length; i++) {
        request_count++;
        let request = request_list[i];

        let div = document.createElement("div");
        div.style.display = "flex";
        div.style.flexDirection = "row";
        div.style.margin = "5px";
        div.style.padding = "10px";
        div.style.background = "#EEEEEE";

        let p = document.createElement("p");
        p.appendChild(document.createTextNode(request.name + " | "));
        let span = document.createElement("span");
        span.style.color = "purple";
        span.innerText = `хочет добавить вас в друзья с ${request.date}`;
        p.appendChild(span);
        div.appendChild(p);

        let button = document.createElement("button");
        button.id = `accept__friend__request__btn__${i}`;
        button.innerText = "Принять";

        div.appendChild(button);

        DivIncomingRequests.appendChild(div);
    }

    for (let i = 0; i < request_list.length; i++) {
        let button = $(`#accept__friend__request__btn__${i}`)[0];
        button.addEventListener("click", () => {
            $.post(`/api/friends/add-friend/${request_list[i].name}`, null, response => {
                getFriendsList();
                getIncomingRequsts();
                getSentRequsts();
            });
        });
    }

    if (request_count > 0) {
        TextIncomingRequestsCount.style.background = "red";
        TextIncomingRequestsCount.style.color = "white";
    } else {
        TextIncomingRequestsCount.style.background = "white";
        TextIncomingRequestsCount.style.color = "black";
    }

    TextIncomingRequestsCount.innerText = request_count.toString();
}

function setSentRequestsListHTML(request_list) {
    TextSentRequestsCount.innerText = "0";
    let request_count = 0;
    if (request_list.length > 0) {
        DivSentRequests.innerHTML = "";
    } else {
        DivSentRequests.innerHTML = "<p style='color: gray'>Исходящих запросов нет</p>";
    }
    for (let request of request_list) {
        let p = document.createElement("p");
        p.style.margin = "5px";
        p.style.padding = "10px";
        p.style.background = "#EEEEEE";

        p.appendChild(document.createTextNode(request.name + " | "));

        let span = document.createElement("span");
        span.style.color = "gray";
        span.innerText = `запрос ожидает ответа с ${request.date}`;
        p.appendChild(span);

        DivSentRequests.appendChild(p);
    }
    TextSentRequestsCount.innerText = request_count.toString();
}

function sendFriendRequest() {
    user_id = InputNewFriendName.value;
    if (user_id.trim() === "") {
        TextNewFriendRequestStatus.style.color = "red";
        TextNewFriendRequestStatus.innerText = "Поле не должно быть пустым";
    } else {
        TextNewFriendRequestStatus.style.color = "gray";
        TextNewFriendRequestStatus.innerText = "Обрабатываем запрос";
        $.post(`api/friends/send-request/${user_id}`, null, response => {
            processNewFriendServerResponse(response);
            getFriendsList();
            getIncomingRequsts();
            getSentRequsts();
        });
    }

}

function processNewFriendServerResponse(response) {
    if (response.success == null) {
        TextNewFriendRequestStatus.innerText = response.error;
        TextNewFriendRequestStatus.style.color = "red";
    } else {
        TextNewFriendRequestStatus.innerText = response.success;
        TextNewFriendRequestStatus.style.color = "green";
        InputNewFriendName.value = "";
    }
}

getFriendsList();
getIncomingRequsts();
getSentRequsts();
ButtonSendNewRequest.addEventListener("click", sendFriendRequest);
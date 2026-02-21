function setCookie(name, value, options = {}) {

    options = {
        path: '/',
        ...options
    };

    if (options.expires instanceof Date) {
        options.expires = options.expires.toUTCString();
    }

    let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value);

    for (let optionKey in options) {
        updatedCookie += "; " + optionKey;
        let optionValue = options[optionKey];
        if (optionValue !== true) {
            updatedCookie += "=" + optionValue;
        }
    }

    document.cookie = updatedCookie;
}

function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

function deleteCookie(name) {
    setCookie(name, "", {
        'max-age': -1
    })
}

function redirectIfValidCredentials(link, elseDo){
    $.ajax({
        url:'api/check-credentials',
        method:'get',
        headers: {
            "email" : getCookie("email"),
            "password_hash" : getCookie("password_hash")
        },
        contentType : 'text/plain',
        success : response => {
            if('1' === response){
                window.location.href = link;
            } else {
                elseDo();
            }
        }
    });
}

function redirectIfBadCredentials(link, elseDo){
    $.ajax({
        url:'api/check-credentials',
        method:'get',
        headers: {
            "email" : getCookie("email"),
            "password_hash" : getCookie("password_hash")
        },
        contentType : 'text/plain',
        success : response => {
            if('0' === response){
                window.location.href = link;
            } else {
                elseDo();
            }
        }
    });
}
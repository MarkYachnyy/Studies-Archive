PInCartInfo = $(".p__in__cart__data")[0];
SpanProductName = $(".span__product__name")[0];
ASellerName = $(".link__seller")[0];
ButtonPlus = $(".button__plus__one")[0];
ButtonMinus = $(".button__minus__one")[0];
ButtonAddToCart = $(".button__add__to__cart")[0];

params = new URLSearchParams(window.location.search);

ButtonPlus.addEventListener("click", () => {
    $.ajax({
        url:`/api/cart/add?productId=${params.get("id")}`,
        method:'post',
        headers:{
            "email":getCookie("email"),
            "password_hash": getCookie("password_hash")
        },
        success : () => {
            getInCartInfo();
        }
    });
});
ButtonMinus.addEventListener("click", () => {
    $.ajax({
        url:`/api/cart/remove?productId=${params.get("id")}`,
        method:'post',
        headers:{
            "email":getCookie("email"),
            "password_hash": getCookie("password_hash")
        },
        success : () => {
            getInCartInfo();
        }
    });
});
ButtonAddToCart.addEventListener("click", () => {
    $.ajax({
        url:`/api/cart/add?productId=${params.get("id")}`,
        method:'post',
        headers:{
            "email":getCookie("email"),
            "password_hash": getCookie("password_hash")
        },
        success : () => {
            getInCartInfo();
        }
    });
});

function getProductInfoAjax(){
    $.ajax({
        url:`api/product?id=${params.get("id")}`,
        method:'get',
        contentType : 'application/json',
        success : product => {
            getSellerInfoAjax(product.sellerId);
            setProductHTML(product);
            getInCartInfo();
        }
    });
}

function getSellerInfoAjax(sellerId){
    $.ajax({
        url:`api/seller?id=${sellerId}`,
        method:'get',
        contentType : 'application/json',
        success : seller => {
            ASellerName.innerText = seller.name;
            ASellerName.href = `/seller?id=${seller.id}`;
        }
    });
}

function getInCartInfo() {
    if(getCookie("email") && getCookie("password_hash")){
        $.ajax({
            url:`api/cart/quantity?productId=${params.get("id")}`,
            method:'get',
            headers:{
                "email":getCookie("email"),
                "password_hash": getCookie("password_hash")
            },
            contentType : 'text/plain',
            success : response => processCartInfo(response)
        });
    }
}

function setProductHTML(product){
    SpanProductName.innerText = product.name;
}

function processCartInfo(response){
    let quantity = parseInt(response);
    if(quantity > 0){
        ButtonPlus.style.display = 'inline';
        ButtonMinus.style.display = 'inline';
        ButtonAddToCart.style.display = 'none';
        PInCartInfo.style.display = 'inline'
        PInCartInfo.innerText = "В корзине: " + quantity;
    } else {
        ButtonPlus.style.display = 'none';
        ButtonMinus.style.display = 'none';
        ButtonAddToCart.style.display = 'inline';
        PInCartInfo.style.display = 'none';
    }
}

getProductInfoAjax();
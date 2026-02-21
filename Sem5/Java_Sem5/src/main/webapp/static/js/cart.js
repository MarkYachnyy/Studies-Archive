DivContent = $(".content")[0];
SpanTotalQuantity = $(".span__total__quantity")[0];
SpanTotalPrice = $(".span__total__price")[0];
ButtonProceedToCheckout = $(".button__proceed__to__checkout")[0];

TotalPrice = 0;
TotalQuantity = 0;

redirectIfBadCredentials("/login", loadCartData);

function loadCartData(){

    $.ajax({
        url:'/api/cart',
        method:'get',
        headers:{
            "email":getCookie("email"),
            "password_hash": getCookie("password_hash")
        },
        contentType : 'application/json',
        success : response => {
            setCartListHtml(response);
        }
    });
}

function setCartListHtml(item_list){
    TotalPrice = 0;
    TotalQuantity = 0;
    DivContent.innerHTML="";
    for(let item of item_list){
        DivContent.innerHTML += makeCartItemHTML(item);
        TotalQuantity += item.quantity;
        TotalPrice += item.quantity * item.price;
    }
    for(let item of item_list){
        $(`#button-minus-one-${item.productId}`)[0].addEventListener("click", () => {
            $.ajax({
                url:`/api/cart/remove?productId=${item.productId}`,
                method:'post',
                headers:{
                    "email":getCookie("email"),
                    "password_hash": getCookie("password_hash")
                },
                success : () => {
                    loadCartData();
                }
            });
        });
        $(`#button-plus-one-${item.productId}`)[0].addEventListener("click", () => {
            $.ajax({
                url:`/api/cart/add?productId=${item.productId}`,
                method:'post',
                headers:{
                    "email":getCookie("email"),
                    "password_hash": getCookie("password_hash")
                },
                success : () => {
                    loadCartData();
                }
            });
        });
    }
    SpanTotalQuantity.innerText = "" + TotalQuantity;
    SpanTotalPrice.innerText = "" + TotalPrice;

    if(TotalQuantity === 0){
        ButtonProceedToCheckout.style.display = 'none';
    } else {
        ButtonProceedToCheckout.style.display = 'inline';
    }
}

function makeCartItemHTML(item){
    let html =
        `<div class="div__cart__item" id="div-cart-item-${item.productId}">
            <img src="/static/images/jonkler.webp" class="image__cart__item">
            <a class="a__cart__item__product__name" href="/product?id=${item.productId}">${item.productName}</a>
            <button class="button__minus__one" id="button-minus-one-${item.productId}">-</button>
            <p class="p__cart__item__quantity">${item.quantity}</p>
            <button class="button__plus__one" id="button-plus-one-${item.productId}">+</button> 
            <p class="p__cart__item__price"> x ${item.price} шт.</p>
        </div>`;
    return html;
}
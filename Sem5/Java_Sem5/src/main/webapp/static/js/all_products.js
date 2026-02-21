DivContent = $(".content")[0];

$.ajax({
    url:'/api/product/all',
    method:'get',
    contentType : 'application/json',
    success : response => {
        setProductListHtml(response);
    }
});

function setProductListHtml(response){
    for(let product of response){
        DivContent.innerHTML += makeProductHtml(product);
        $(`#div-product-${product.id}`)[0].addEventListener("click", () => {
            window.location.href = `/product?id=${product.id}`;
        });
    }
    for(let product of response){
        $(`#div-product-${product.id}`)[0].addEventListener("click", () => {
            window.location.href = `/product?id=${product.id}`;
        });
    }
}

function makeProductHtml(product){
    let html =
        `<div class="div__product" id="div-product-${product.id}">
            <img alt="product" src="/static/images/jonkler.webp" class="image__product">
            <p class="p__product__name">${product.name}</p>
            <p class="p__product__price">${product.price} р</p>
            <p class="p__product__in__stock">${product.stockQuantity} в наличии</p>
        </div>`
    return html;
}
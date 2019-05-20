---
--- 应用层nginx根据缓存渲染末班后返回给分发层nginx
--- Created by gaowenwen.
--- DateTime: 2019-5-16 21:02
---
local cjson = require("cjson")
local producer = require("resty.kafka.producer")
local broker_list = {
    { host = "node128", port = 9092 },
    { host = "node129", port = 9092 }
}
function sendAccessLogToKafka()
    local log_json = {}
    log_json["headers"] = ngx.req.get_headers()
    log_json["uri_args"] = ngx.req.get_uri_args()
    log_json["body"] = ngx.req.read_body()
    log_json["http_version"] = ngx.req.http_version()
    log_json["method"] =ngx.req.get_method()
    log_json["raw_header"] = ngx.req.raw_header()
    log_json["body_data"] = ngx.req.get_body_data()
    local message = cjson.encode(log_json);
    local productId = ngx.req.get_uri_args()["productId"]

    local async_producer = producer:new(broker_list, { producer_type = "async" })
    local ok, err = async_producer:send("AccessLog", productId, message)
    if not ok then
        ngx.log(ngx.ERR, "kafka send err:", err)
        return
    end
end

-- 上报流量
sendAccessLogToKafka()

-- 获取请求参数table
local uri_args = ngx.req.get_uri_args()
local productId = uri_args["productId"]
local shopId = uri_args["shopId"]

-- 获取本地nginx共享缓存
local cache_ngx = ngx.shared.my_cache

-- 缓存的Key
local productCacheKey = "product_info_" .. productId
local shopCacheKey = "shop_info_" .. shopId
-- 获取本地缓存值
local productCache = cache_ngx:get(productCacheKey)
local shopCache = cache_ngx:get(shopCacheKey)

if productCache == "" or productCacheKey == nil then
    local http = require("resty.http")
    local client = http.new()
    -- 从缓存生产服务获取redis数据
    local resp, err = client:request_uri("http://192.168.211.1:8080/", {
        method = "GET",
        path = "/getProductInfo/" .. productId
    })
    productCache = resp.body
    cache_ngx:set(productCacheKey, productCache, 10 * 60)
end

if shopCache == "" or shopCache == nil then
    local http = require("resty.http")
    local client = http.new()
    -- 从缓存生产服务获取redis数据
    local resp, err = client:request_uri("http://192.168.211.1:8080/", {
        method = "GET",
        path = "/getShopInfo/" .. shopId
    })
    shopCache = resp.body
    cache_ngx:set(shopCacheKey, shopCache, 10 * 60)
end

local cjson = require("cjson")
local productCacheJSON = cjson.decode(productCache)
local shopCacheJSON = cjson.decode(shopCache)
local ctx = {
    productId = productCacheJSON.id,
    productName = productCacheJSON.name,
    productPrice = productCacheJSON.price,
    productPictureList = productCacheJSON.pictures,
    productSpecification = productCacheJSON.specification,
    productService = productCacheJSON.service,
    productColor = productCacheJSON.color,
    productSize = productCacheJSON.size,
    shopId = shopCacheJSON.id,
    shopName = shopCacheJSON.name,
    shopLevel = shopCacheJSON.level,
    shopGoodCommentRate = shopCacheJSON.goodCommentRate,
}
-- 渲染模板并返回html文件
local template = require("resty.template")
template.render("product.html", ctx)

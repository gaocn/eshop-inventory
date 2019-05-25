local uri_args = ngx.req.get_uri_args()
local productId = uri_args["productId"]
local shopId = uri_args["shopId"]
local requestPath = uri_args["requestPath"]
-- 后端应用服务器地址
local hosts = {"192.168.211.129:9080", "192.168.211.129:9080"}
local backend = ""

-- 获取本地缓存的热点productId
local cache_ngx = ngx.shared.my_cache
local hot_product_cache_key = "hot_product_" .. product_id
local is_hot_product = cache_ngx:get(hot_product_cache_key)
if is_hot_product == true then
    math.randomseed(tostring(os.time()):reverse():sub(1, 7))
    local idx = math.random(1, 2)
    backend = "http://" .. hosts[idx]
else
    local hash = ngx.crc32_long(productId)
    local idx = (hash % 2) + 1
    backend = "http://" .. hosts[idx]
end
requestPath = "/" .. requestPath .. "?productId=" .. productId .. "&shopId=" ..shopId
local http = require("resty.http")
local httpc = http.new()

local resp, err = httpc:request_uri(backend, {
    method = "GET",
    path = requestPath
})

if  not resp then
    ngx.say("requset error: ", err)
end
-- 后端返回结果
ngx.say(resp.body)
httpc:close()

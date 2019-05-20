local uri_args = ngx.req.get_uri_args()

local productId = uri_args["productId"]
local shopId = uri_args["shopId"]
local requestPath = uri_args["requestPath"]
-- 后端应用服务器地址
local hosts = {"192.168.211.129:9080", "192.168.211.129:9080"}
local hash = ngx.crc32_long(productId)
local idx = (hash % 2) + 1
backend = "http://" .. hosts[idx]
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

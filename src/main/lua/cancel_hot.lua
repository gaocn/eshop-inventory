---
--- Created by gaowenwen.
--- DateTime: 2019-5-22 0:06
---

local uri_args = ngx.req.get_uri_args()
local product_id = uri_args["productId"]

local hot_product_cache_key = "hot_product_" .. product_id
local cache_ngx = ngx.shared.my_cache
--缓存过期时间单位：秒
cache_ngx:set(hot_product_cache_key,"false", 10)
ngx.say("cancel hot cache productId=" .. product_id)
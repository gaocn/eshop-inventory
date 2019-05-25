---
--- Created by gaowenwen.
--- DateTime: 2019-5-21 22:51
---


--
-- 分发层nginx hot.lua脚本
--
local uri_args = ngx.req.get_uri_args()
local product_id = uri_args["productId"]

local hot_product_cache_key = "hot_product_" .. product_id
local cache_ngx = ngx.shared.my_cache
cache_ngx:set(hot_product_cache_key, true, 60 * 60)

--
-- 应用层nginx hot.lua脚本
--
local uri_args = ngx.req.get_uri_args()
local product_id = uri_args["productId"]
local product_info = uri_args["productInfo"]

local product_cache_key = "product_info_" .. product_id
local cache_ngx = ngx.shared.my_cache
cache_ngx:set(product_cache_key, product_info, 60*60)
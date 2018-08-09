package app.xlui.seckill.cloud.service.config;

import app.xlui.seckill.cloud.service.entity.Response;

public class Const {
    public static final Response SUCCESS = new Response().append("status", "success").append("msg", "successfully seckill an item!!!");
    public static final Response END = new Response().append("status", "end").append("msg", "seckill is end!");
    public static final Response MUCH = new Response().append("status", "give up").append("msg", "Tooooo many people, try again later.");
}

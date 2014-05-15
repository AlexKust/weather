module("luci.controller.weather.weather", package.seeall)
function index()
    entry({"weather"}, call("get_current")).dependent=false
    entry({"weather", "forecast"}, call("get_forecast")).dependent=false
    entry({"weather", "current"}, call("get_current")).dependent=false    
end

function get_forecast()
    luci.http.prepare_content("application/json")
    file = io.popen("/root/scripts/weather/gismeteo")
    output = file:read('*all')
    luci.http.write(output)
end

function get_current()
    luci.http.prepare_content("application/json")
    file = io.popen("/root/scripts/weather/current")
    output = file:read('*all')
    luci.http.write(output)
end

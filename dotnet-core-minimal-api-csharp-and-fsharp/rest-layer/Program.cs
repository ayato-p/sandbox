using System.Text.Json.Serialization;
using Demoapp.Handler;

var builder = WebApplication.CreateSlimBuilder(args);

builder.Services.ConfigureHttpJsonOptions(options =>
{
    options.SerializerOptions.TypeInfoResolverChain.Insert(0, AppJsonSerializerContext.Default);
});

var app = builder.Build();

app.MapGet("/ping", PingPong.Handler);
app.MapGet("/echo/{name}", Echo.Handler);

app.Run();


[JsonSerializable(typeof(PingPongResponse))]    
internal partial class AppJsonSerializerContext : JsonSerializerContext
{

}

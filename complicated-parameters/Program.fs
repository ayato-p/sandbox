open System
open System.Net.Http
open Microsoft.AspNetCore.Builder
open Microsoft.AspNetCore.Http
open Microsoft.Extensions.Hosting
open Microsoft.Extensions.DependencyInjection

open App.Driver
open App.Controller

[<EntryPoint>]
let main args =
    let builder = WebApplication.CreateBuilder(args)

    builder.Services.AddHttpClient<ExampleDriver1>(fun client -> client.BaseAddress <- Uri "http://example.com")
    |> ignore

    builder.Services.AddHttpClient<ExampleDriver2>(fun client -> client.BaseAddress <- Uri "http://example.com")
    |> ignore

    builder.Services.AddHttpClient<HttpClient>(
        "ExampleDriver3",
        fun client -> client.BaseAddress <- Uri "http://example.com"
    )
    |> ignore

    let app = builder.Build()

    app.MapPost(
        "/articles",
        Func<ExampleDriver1, Article, IHttpClientFactory, ExampleDriver2, IResult>
            (fun driver1 article factory driver2 -> Results.Created("/articles/1", article.ToString()))
    )
    |> ignore

    app.Run()

    0 // Exit code

open System
open System.Threading.Tasks
open System.Net.Http
open System.Text.Json
open Microsoft.AspNetCore.Builder
open Microsoft.Extensions.Hosting
open Microsoft.Extensions.DependencyInjection
open Microsoft.AspNetCore.Http

module Driver =

    type ClientA(client: HttpClient) =
        member _.client = client

    type Response = { publishingOffice: string }

    let forecast (client: HttpClient) (office: int) : Async<Response list> =
        // https://www.jma.go.jp/bosai/forecast/data/forecast/130000.json
        task {
            use! response =
                new HttpRequestMessage(HttpMethod.Get, sprintf "/bosai/forecast/data/forecast/%d.json" office)
                |> (fun req -> client.SendAsync(req, HttpCompletionOption.ResponseHeadersRead))

            match response.IsSuccessStatusCode with
            | true ->
                use! json = response.Content.ReadAsStreamAsync()
                return! JsonSerializer.DeserializeAsync<Response list>(json)
            | false -> return (failwith "")
        }
        |> Async.AwaitTask

module Async =
    let map (f: 'T -> 'R) (a: Async<'T>) : Async<'R> =
        async {
            let! x = a
            return f x
        }

[<EntryPoint>]
let main args =
    let builder = WebApplication.CreateBuilder(args)

    builder
        .Services
        .AddHttpClient<Driver.ClientA>(fun client ->
            client.BaseAddress <- Uri "https://www.jma.go.jp/"
            client.DefaultRequestHeaders.Add(Microsoft.Net.Http.Headers.HeaderNames.Accept, "applicatoin/json"))
        .ConfigurePrimaryHttpMessageHandler(
            Func<HttpMessageHandler> (fun () ->
                new SocketsHttpHandler(
                    Proxy = null,
                    UseProxy = false,
                    PooledConnectionIdleTimeout = TimeSpan.FromMinutes(10),
                    PooledConnectionLifetime = TimeSpan.FromMinutes(10),
                    PreAuthenticate = false,
                    UseCookies = false,
                    MaxConnectionsPerServer = 1000
                ))
        )
    |> ignore

    let app = builder.Build()

    app.MapGet(
        "/forecast/a",
        Func<Driver.ClientA, Task<IResult>> (fun client ->
            Driver.forecast client.client 130000
            |> Async.map Results.Ok
            |> Async.StartAsTask)
    )
    |> ignore

    app.MapGet(
        "/warmup",
        Func<Driver.ClientA, Task<unit>> (fun (client) ->
            async {
                do!
                    Driver.forecast client.client 130000
                    |> Async.map ignore
            }
            |> Async.StartAsTask)
    )
    |> ignore

    app.Run()

    0 // Exit code

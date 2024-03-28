using System.Text.Json.Serialization;

namespace Demoapp.Handler;

public record PingPongResponse(string Pong);

class PingPong
{
    public static IResult Handler() {
        return Results.Ok(new PingPongResponse("Pong!"));
    } 
}

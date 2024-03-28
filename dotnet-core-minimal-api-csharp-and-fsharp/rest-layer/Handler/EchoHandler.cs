namespace Demoapp.Handler;

using Demoapp.MyGreatUsecase;

class Echo {
    public static async Task<IResult> Handler(string name) {
        var result = await EchoUsecase.execute(name);
        if(result.IsOk) {
            return Results.Ok(result.ResultValue);
        }else {
            return Results.Problem(result.ErrorValue);
        }
    }
}   
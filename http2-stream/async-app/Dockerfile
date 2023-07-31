FROM mcr.microsoft.com/dotnet/sdk:7.0 as build
WORKDIR /build
COPY . /build
RUN dotnet build App -c Release

FROM mcr.microsoft.com/dotnet/aspnet:7.0
COPY --from=build /build/App/bin/Release/net7.0/ /usr/local/app 
CMD ["/usr/local/app/App"]
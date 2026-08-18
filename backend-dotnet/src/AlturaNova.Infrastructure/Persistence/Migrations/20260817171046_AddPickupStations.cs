using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AlturaNova.Infrastructure.Persistence.Migrations
{
    /// <inheritdoc />
    public partial class AddPickupStations : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "DeliveryMethod",
                table: "orders",
                type: "character varying(20)",
                maxLength: 20,
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<Guid>(
                name: "PickupStationId",
                table: "orders",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddColumn<decimal>(
                name: "ShippingFee",
                table: "orders",
                type: "numeric(18,2)",
                precision: 18,
                scale: 2,
                nullable: false,
                defaultValue: 0m);

            migrationBuilder.CreateTable(
                name: "pickup_stations",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: false),
                    Address = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: false),
                    City = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    Phone = table.Column<string>(type: "character varying(40)", maxLength: 40, nullable: true),
                    OperatingHours = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: true),
                    IsActive = table.Column<bool>(type: "boolean", nullable: false),
                    CreatedAt = table.Column<DateTimeOffset>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_pickup_stations", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_orders_PickupStationId",
                table: "orders",
                column: "PickupStationId");

            migrationBuilder.CreateIndex(
                name: "IX_pickup_stations_IsActive",
                table: "pickup_stations",
                column: "IsActive");

            migrationBuilder.AddForeignKey(
                name: "FK_orders_pickup_stations_PickupStationId",
                table: "orders",
                column: "PickupStationId",
                principalTable: "pickup_stations",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_orders_pickup_stations_PickupStationId",
                table: "orders");

            migrationBuilder.DropTable(
                name: "pickup_stations");

            migrationBuilder.DropIndex(
                name: "IX_orders_PickupStationId",
                table: "orders");

            migrationBuilder.DropColumn(
                name: "DeliveryMethod",
                table: "orders");

            migrationBuilder.DropColumn(
                name: "PickupStationId",
                table: "orders");

            migrationBuilder.DropColumn(
                name: "ShippingFee",
                table: "orders");
        }
    }
}

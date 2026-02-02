-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 02, 2026 at 01:23 PM
-- Wersja serwera: 10.4.32-MariaDB
-- Wersja PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tennis_ranking`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `administrator`
--

CREATE TABLE `administrator` (
  `id_administratora` varchar(20) NOT NULL,
  `id_konta` varchar(20) NOT NULL,
  `imie` varchar(25) NOT NULL,
  `nazwisko` varchar(25) NOT NULL,
  `email` varchar(190) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `administrator`
--

INSERT INTO `administrator` (`id_administratora`, `id_konta`, `imie`, `nazwisko`, `email`) VALUES
('ADM1', 'ACC_A1', 'Adam', 'Admin', 'admin@local');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `kibic`
--

CREATE TABLE `kibic` (
  `id_kibica` varchar(20) NOT NULL,
  `id_konta` varchar(20) NOT NULL,
  `pseudonim` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `kibic`
--

INSERT INTO `kibic` (`id_kibica`, `id_konta`, `pseudonim`) VALUES
('KIB_542403aebd48466a', 'ACC_c127b2c63f744846', 'aha'),
('KIB1', 'ACC_K1', 'Kibic123');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `konto`
--

CREATE TABLE `konto` (
  `id_konta` varchar(20) NOT NULL,
  `login` varchar(30) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `rola` enum('ADMIN','ORGANIZATOR','SEDZIA','ZAWODNIK','KIBIC') NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `konto`
--

INSERT INTO `konto` (`id_konta`, `login`, `password_hash`, `rola`, `enabled`, `created_at`) VALUES
('ACC_12a7e5b16f514e37', 'siema6', '$2a$10$x6aaAt6OoKrywXi9o9b5NeUGZbSc.mVU2/uE5tlAksL4MRurGCQ92', 'ZAWODNIK', 1, '2026-01-29 21:26:56'),
('ACC_2a2c9403241c4b37', 'Zuzanna', '$2a$10$.Xeds4wRN2HVXVD3oG2YqOSMgJAlsOkWN0NMsAkPizxasESzeEsCa', 'ZAWODNIK', 1, '2025-12-18 22:49:43'),
('ACC_3483e7b30c734fb4', 'siemanderson', '$2a$10$W8JTZjaR2DbFzRto.vjUQeWt2tQ2yOXFKeJCuxmn/IZLOf2NicRqi', 'SEDZIA', 1, '2026-01-15 21:51:29'),
('ACC_385228cb4eb1474d', 'siema3', '$2a$10$CIVOvGP1g.mA8kZDLXeWbuCIEyz6FhgRf4Z0HlqfWT8GrhmORkOSe', 'ZAWODNIK', 1, '2026-01-29 21:26:00'),
('ACC_5ac54b5c8a9b49c8', 'siema7', '$2a$10$5uWlBg1BKeTt5cgwkse1N.rd235EORlMSWCI0NPNP5r4Nz8Z8CN5u', 'ZAWODNIK', 1, '2026-01-29 21:27:11'),
('ACC_5eac588c35ce4d71', 'siema8', '$2a$10$bmEtIumhV6OP532J07RsJefp9rMqRYQn.FCeH9wjqMG78fQl3NaKq', 'ZAWODNIK', 1, '2026-01-29 21:27:35'),
('ACC_73cb894d78bd4839', 'siema1', '$2a$10$V7tS/oWZl0LPqXQ.SdSGSeoBPBoyC.EJU1thXweRNllLM4SA6WfJm', 'ORGANIZATOR', 1, '2025-12-18 22:00:01'),
('ACC_9c7dbbb66fb14a81', 'siemandero1', '$2a$10$kEBbhkI7P7/OuFl9JANFHesWVnEzgGMTBfNSy7Y0MVrYmcr20J85y', 'ORGANIZATOR', 1, '2026-01-15 19:56:44'),
('ACC_A1', 'admin', '$2a$10$i27QkbVg/o0tskOiQ0ri6uwOOQ0.lDC4kLjmhSZllt7MSkFjE/r6G', 'ADMIN', 1, '2025-12-18 21:27:25'),
('ACC_c127b2c63f744846', 'ahaaha', '$2a$10$zwzuSC5L2MnhBJVNDol74eaItV4vDOmpHKuYTlOlpdA0Y.LDHslaW', 'KIBIC', 1, '2026-01-15 22:35:20'),
('ACC_c7f2ae15171545c0', 'siema4', '$2a$10$3f.Y/VtaaNDUCmQDKNS0seJk4vi37h8gjV.zGJ4ptCr8PEF2L/8iG', 'ZAWODNIK', 1, '2026-01-29 21:26:22'),
('ACC_e12becc25dd141fd', 'siema2', '$2a$10$B01WLFZXDT2C2TYS8.pxme0U0K5SZENpYtSlaQbGXFqrcYaSw1zEC', 'ZAWODNIK', 1, '2026-01-29 21:25:36'),
('ACC_f2517ec915dd416c', 'siema5', '$2a$10$n8cKw05RCaK9mXXf1DuXTO3wp6xbSbRKuIw1qZqCcyglYCmvGNMLy', 'ZAWODNIK', 1, '2026-01-29 21:26:40'),
('ACC_f9848f9ccbee4205', 'igas', '$2a$10$Rhnjmv7DDF6o0x/q2Xv/VuJ/KFCDGvg5v5IfgwWKLQcYUf2ErmeM.', 'ZAWODNIK', 1, '2026-01-30 14:41:45'),
('ACC_K1', 'kibic', 'TEST_HASH', 'KIBIC', 1, '2025-12-18 21:27:25'),
('ACC_O1', 'org', 'TEST_HASH', 'ORGANIZATOR', 1, '2025-12-18 21:27:25'),
('ACC_S1', 'sedzia', 'TEST_HASH', 'SEDZIA', 1, '2025-12-18 21:27:25');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `mecz`
--

CREATE TABLE `mecz` (
  `id_meczu` varchar(20) NOT NULL,
  `id_turnieju` varchar(20) NOT NULL,
  `runda` int(11) NOT NULL,
  `slot_w_rundzie` int(11) NOT NULL,
  `id_zawodnik_a` varchar(20) DEFAULT NULL,
  `id_zawodnik_b` varchar(20) DEFAULT NULL,
  `seed_a` int(11) DEFAULT NULL,
  `seed_b` int(11) DEFAULT NULL,
  `id_zwyciezcy` varchar(20) DEFAULT NULL,
  `wynik` varchar(50) DEFAULT NULL,
  `id_sedzia` varchar(20) DEFAULT NULL,
  `status` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `mecz`
--

INSERT INTO `mecz` (`id_meczu`, `id_turnieju`, `runda`, `slot_w_rundzie`, `id_zawodnik_a`, `id_zawodnik_b`, `seed_a`, `seed_b`, `id_zwyciezcy`, `wynik`, `id_sedzia`, `status`) VALUES
('08e0931709544cd28c12', '5691dd5192cd41ca9191', 1, 2, NULL, NULL, 2, 3, NULL, '6:4 6:3', NULL, 'ZAKONCZONY'),
('1a4f844f57124c79a7a3', 'da93b4cd2604404b9564', 1, 1, NULL, NULL, 1, 2, NULL, '6:4 6:3', NULL, 'ZAKONCZONY'),
('1ae6a6360a664e5ab172', '5691dd5192cd41ca9191', 2, 1, NULL, NULL, NULL, NULL, NULL, '', NULL, 'ZAKONCZONY'),
('1cbe0232048943cd9e26', '5691dd5192cd41ca9191', 1, 1, NULL, NULL, 1, 4, NULL, '6:4 6:4', NULL, 'ZAKONCZONY'),
('23a8ca3f263047d98edb', '3da94038fc2146e0b8bd', 1, 1, NULL, NULL, 1, 8, NULL, '', NULL, 'ZAKONCZONY'),
('27581fe4a53e443c9be8', '4e67568f4dc141ecac5e', 1, 2, NULL, NULL, 4, 5, NULL, '3:6 6:3 6:4', NULL, 'ZAKONCZONY'),
('3148751c676747469ca2', '4e67568f4dc141ecac5e', 1, 4, NULL, NULL, 3, 6, NULL, '6:4 7:6(7-5)', NULL, 'ZAKONCZONY'),
('33c3157377344580affa', '9355bf5959f6432c9fea', 1, 1, NULL, NULL, 1, 2, NULL, '6:4 6:4', NULL, 'ZAKONCZONY'),
('7561457d6a3a4957922e', '08b8e8e7f7c447efb450', 1, 2, 'ZAW_469b9890a6ed4ec2', 'ZAW_5c3eeb6c15eb4416', 4, 5, 'ZAW_469b9890a6ed4ec2', '', NULL, 'ZAKONCZONY'),
('9015fe9fcd784eb7aa24', '08b8e8e7f7c447efb450', 1, 1, 'ZAW_07e1611cfd84402a', 'ZAW_e420e48865f04484', 1, 8, 'ZAW_07e1611cfd84402a', '', NULL, 'ZAKONCZONY'),
('9095c83af1e94b78a241', '08b8e8e7f7c447efb450', 1, 3, 'ZAW_0a8212f3a34e40d1', 'ZAW_d2962d81f9e848a4', 2, 7, 'ZAW_0a8212f3a34e40d1', '', NULL, 'ZAKONCZONY'),
('93d7b17dff7344bca0ba', '08b8e8e7f7c447efb450', 3, 1, 'ZAW_07e1611cfd84402a', 'ZAW_a8ac6b6e58cb44e1', NULL, NULL, 'ZAW_07e1611cfd84402a', '', NULL, 'ZAKONCZONY'),
('96256925f9c646fe9efd', '4e67568f4dc141ecac5e', 3, 1, NULL, NULL, NULL, NULL, NULL, '6:0 6:0', NULL, 'ZAKONCZONY'),
('a1bce97820c34f73a351', '6f2693fbfca742c2be3f', 1, 1, 'ZAW_0a8212f3a34e40d1', 'ZAW_e420e48865f04484', 1, 2, 'ZAW_e420e48865f04484', '', NULL, 'ZAKONCZONY'),
('a5d5df3b01b844029d96', '08b8e8e7f7c447efb450', 2, 1, 'ZAW_07e1611cfd84402a', 'ZAW_469b9890a6ed4ec2', NULL, NULL, 'ZAW_07e1611cfd84402a', '', NULL, 'ZAKONCZONY'),
('a620e31f9b5f4cf2b11d', '3da94038fc2146e0b8bd', 1, 4, NULL, NULL, 3, 6, NULL, '', NULL, 'ZAKONCZONY'),
('b79346fbfda548a9a71d', '3da94038fc2146e0b8bd', 3, 1, NULL, NULL, NULL, NULL, NULL, '', NULL, 'ZAKONCZONY'),
('be54d81c007542968a75', '4e67568f4dc141ecac5e', 1, 3, NULL, NULL, 2, 7, NULL, '6:0 6:0', NULL, 'ZAKONCZONY'),
('bf0a2e4786d34987866b', '08b8e8e7f7c447efb450', 1, 4, 'ZAW_0d2b378287bd4d0d', 'ZAW_a8ac6b6e58cb44e1', 3, 6, 'ZAW_a8ac6b6e58cb44e1', '', NULL, 'ZAKONCZONY'),
('c79666495c5a480f9a38', '4e67568f4dc141ecac5e', 1, 1, NULL, NULL, 1, 8, NULL, '6:3 6:1', NULL, 'ZAKONCZONY'),
('d54a018dedf242f7afea', '3da94038fc2146e0b8bd', 2, 1, NULL, NULL, NULL, NULL, NULL, '', NULL, 'ZAKONCZONY'),
('d8394df4bf2b4091af90', '4e67568f4dc141ecac5e', 2, 2, NULL, NULL, NULL, NULL, NULL, '6:0 6:0', NULL, 'ZAKONCZONY'),
('f1bfcdd2405e41ddb4ea', '3da94038fc2146e0b8bd', 1, 3, NULL, NULL, 2, 7, NULL, '', NULL, 'ZAKONCZONY'),
('fada28d9644e41419022', '3da94038fc2146e0b8bd', 1, 2, NULL, NULL, 4, 5, NULL, '', NULL, 'ZAKONCZONY'),
('fbcd358954b94de3a24d', '08b8e8e7f7c447efb450', 2, 2, 'ZAW_0a8212f3a34e40d1', 'ZAW_a8ac6b6e58cb44e1', NULL, NULL, 'ZAW_a8ac6b6e58cb44e1', '', NULL, 'ZAKONCZONY'),
('fec3eca5cfd84e87af3f', '3da94038fc2146e0b8bd', 2, 2, NULL, NULL, NULL, NULL, NULL, '', NULL, 'ZAKONCZONY'),
('ff93f84566dc461bb538', '4e67568f4dc141ecac5e', 2, 1, NULL, NULL, NULL, NULL, NULL, '6:3 6:3', NULL, 'ZAKONCZONY');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `organizator`
--

CREATE TABLE `organizator` (
  `id_organizatora` varchar(20) NOT NULL,
  `id_konta` varchar(20) NOT NULL,
  `imie` varchar(25) NOT NULL,
  `nazwisko` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `organizator`
--

INSERT INTO `organizator` (`id_organizatora`, `id_konta`, `imie`, `nazwisko`) VALUES
('c243e560ff4c491aaed0', 'ACC_A1', 'System', 'Admin'),
('ORG_39b2608c194c4993', 'ACC_9c7dbbb66fb14a81', 'siemandero1', 'siemandero1'),
('ORG_90e80d98a65c4d0d', 'ACC_73cb894d78bd4839', 'Andrzej', 'Stokłosa'),
('ORG1', 'ACC_O1', 'Ola', 'Organizator');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `punkty_log`
--

CREATE TABLE `punkty_log` (
  `id_logu` varchar(20) NOT NULL,
  `id_turnieju` varchar(20) NOT NULL,
  `id_meczu` varchar(20) NOT NULL,
  `id_zawodnika` varchar(20) NOT NULL,
  `typ` varchar(40) NOT NULL,
  `punkty` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `punkty_turniejowe`
--

CREATE TABLE `punkty_turniejowe` (
  `id` varchar(20) NOT NULL,
  `id_turnieju` varchar(20) NOT NULL,
  `id_zawodnika` varchar(20) NOT NULL,
  `opis` varchar(120) DEFAULT NULL,
  `punkty` int(11) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `punkty_turniejowe`
--

INSERT INTO `punkty_turniejowe` (`id`, `id_turnieju`, `id_zawodnika`, `opis`, `punkty`, `updated_at`) VALUES
('346f8ed39b5a4b6cb637', '08b8e8e7f7c447efb450', 'ZAW_d2962d81f9e848a4', 'Udział / 1R', 30, '2026-01-29 23:03:35.000000'),
('47d0097f2b54470eade8', '08b8e8e7f7c447efb450', 'ZAW_a8ac6b6e58cb44e1', 'Awans -> R3', 900, '2026-01-29 23:04:21.000000'),
('791494efb85e44c9bda6', '08b8e8e7f7c447efb450', 'ZAW_0a8212f3a34e40d1', 'Awans -> R2', 540, '2026-01-29 23:04:12.000000'),
('955b05e79c2d4e918b36', '6f2693fbfca742c2be3f', 'ZAW_e420e48865f04484', 'Mistrz', 500, '2026-01-30 15:09:34.000000'),
('9c64f38a5e884846bedb', '08b8e8e7f7c447efb450', 'ZAW_469b9890a6ed4ec2', 'Awans -> R2', 540, '2026-01-29 23:04:07.000000'),
('9f51c8cfa4704e06a1df', '08b8e8e7f7c447efb450', 'ZAW_07e1611cfd84402a', 'Mistrz', 1500, '2026-01-29 23:04:25.000000'),
('c6ab3433651f4ca2b84f', '08b8e8e7f7c447efb450', 'ZAW_0d2b378287bd4d0d', 'Udział / 1R', 30, '2026-01-29 23:03:35.000000'),
('c8938db9833a412696ec', '08b8e8e7f7c447efb450', 'ZAW_e420e48865f04484', 'Udział / 1R', 30, '2026-01-29 23:03:35.000000'),
('dd25224a7a0c47fb99f3', '08b8e8e7f7c447efb450', 'ZAW_5c3eeb6c15eb4416', 'Udział / 1R', 30, '2026-01-29 23:03:35.000000'),
('fb92b806e3854a1cb8be', '6f2693fbfca742c2be3f', 'ZAW_0a8212f3a34e40d1', 'Udział / 1R', 10, '2026-01-30 15:09:09.000000');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `sedzia`
--

CREATE TABLE `sedzia` (
  `id_sedzia` varchar(20) NOT NULL,
  `id_konta` varchar(20) NOT NULL,
  `imie` varchar(25) NOT NULL,
  `nazwisko` varchar(25) NOT NULL,
  `numer_licencji` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sedzia`
--

INSERT INTO `sedzia` (`id_sedzia`, `id_konta`, `imie`, `nazwisko`, `numer_licencji`) VALUES
('SED_d42ab5886bfb4860', 'ACC_3483e7b30c734fb4', 'siemanderson', 'siemanderson', 67),
('SED1', 'ACC_S1', 'Jan', 'Sedzia', 12345);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `sedzia_turniej`
--

CREATE TABLE `sedzia_turniej` (
  `id_turnieju` varchar(20) NOT NULL,
  `id_sedzia` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `turniej`
--

CREATE TABLE `turniej` (
  `id_turnieju` varchar(20) NOT NULL,
  `ranga` varchar(255) NOT NULL,
  `status` enum('OTWARTE_ZAPISY','W_TRAKCIE','ZAKONCZONY','ZAMKNIETE_ZAPISY') NOT NULL,
  `id_organizatora` varchar(20) NOT NULL,
  `nazwa` varchar(60) NOT NULL,
  `punktacja_turnieju` longtext DEFAULT NULL,
  `drabinka_turnieju` longtext DEFAULT NULL,
  `sezon` int(11) NOT NULL,
  `max_zawodnikow` int(11) NOT NULL DEFAULT 32
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `turniej`
--

INSERT INTO `turniej` (`id_turnieju`, `ranga`, `status`, `id_organizatora`, `nazwa`, `punktacja_turnieju`, `drabinka_turnieju`, `sezon`, `max_zawodnikow`) VALUES
('08b8e8e7f7c447efb450', 'ATP_FINALS', 'ZAKONCZONY', 'c243e560ff4c491aaed0', 'ATP FINALS 2026', NULL, NULL, 2026, 8),
('3da94038fc2146e0b8bd', 'ATP_250', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'testttt', NULL, NULL, 2026, 8),
('4e67568f4dc141ecac5e', 'ATP_500', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'Wrocław Open', NULL, NULL, 2026, 8),
('51b698c381214fff925d', 'ATP_1000', 'OTWARTE_ZAPISY', 'ORG_39b2608c194c4993', 'aSdasd', NULL, NULL, 2026, 8),
('5691dd5192cd41ca9191', 'ATP_250', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'asdasd', NULL, NULL, 2026, 4),
('6f2693fbfca742c2be3f', 'ATP_500', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'ASD', NULL, NULL, 2027, 8),
('71df65777ee64dc9ac8a', 'WIELKI_SZLEM', 'OTWARTE_ZAPISY', 'ORG_39b2608c194c4993', 'Kraków Open', NULL, NULL, 2026, 8),
('9355bf5959f6432c9fea', 'ATP_250', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'siema1', NULL, NULL, 2026, 2),
('da93b4cd2604404b9564', 'ATP_1000', 'ZAKONCZONY', 'ORG_39b2608c194c4993', 'Siemandeor', NULL, NULL, 2026, 2);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `zawodnik`
--

CREATE TABLE `zawodnik` (
  `id_zawodnika` varchar(20) NOT NULL,
  `id_konta` varchar(20) NOT NULL,
  `imie` varchar(25) NOT NULL,
  `nazwisko` varchar(25) NOT NULL,
  `punkty` int(11) NOT NULL DEFAULT 0,
  `kraj` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `zawodnik`
--

INSERT INTO `zawodnik` (`id_zawodnika`, `id_konta`, `imie`, `nazwisko`, `punkty`, `kraj`) VALUES
('ZAW_07e1611cfd84402a', 'ACC_2a2c9403241c4b37', 'Zuzanna', 'Kotek', 0, 'DE'),
('ZAW_0a8212f3a34e40d1', 'ACC_e12becc25dd141fd', 'Tomasz', 'Smokowski', 10, 'PL'),
('ZAW_0d2b378287bd4d0d', 'ACC_c7f2ae15171545c0', 'Jarek', 'Tusk', 0, 'PL'),
('ZAW_3d4dd65eade749e2', 'ACC_f9848f9ccbee4205', 'Iga', 'Swiatek', 0, 'PL'),
('ZAW_469b9890a6ed4ec2', 'ACC_f2517ec915dd416c', 'Tobiasz', 'Świątek', 0, 'PL'),
('ZAW_5c3eeb6c15eb4416', 'ACC_12a7e5b16f514e37', 'Piotr', 'Nowacki', 0, 'PL'),
('ZAW_a8ac6b6e58cb44e1', 'ACC_5eac588c35ce4d71', 'Maciej', 'Nowak', 0, 'PL'),
('ZAW_d2962d81f9e848a4', 'ACC_5ac54b5c8a9b49c8', 'Wojciech', 'Konieczny', 0, 'PL'),
('ZAW_e420e48865f04484', 'ACC_385228cb4eb1474d', 'Karol', 'Nawrot', 500, 'PL');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `zgloszenie`
--

CREATE TABLE `zgloszenie` (
  `id_zgloszenia` varchar(20) NOT NULL,
  `id_zawodnika` varchar(20) NOT NULL,
  `id_turnieju` varchar(20) NOT NULL,
  `status` enum('ZAREJESTROWANY','WYCOFANY') NOT NULL DEFAULT 'ZAREJESTROWANY',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `zgloszenie_turniejowe`
--

CREATE TABLE `zgloszenie_turniejowe` (
  `id_zgloszenia` varchar(20) NOT NULL,
  `id_turnieju` varchar(20) NOT NULL,
  `id_zawodnika` varchar(20) NOT NULL,
  `status` varchar(30) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `zgloszenie_turniejowe`
--

INSERT INTO `zgloszenie_turniejowe` (`id_zgloszenia`, `id_turnieju`, `id_zawodnika`, `status`, `created_at`) VALUES
('0a764833689c4d5abae3', '08b8e8e7f7c447efb450', 'ZAW_07e1611cfd84402a', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('3747241171554fa49a1d', '08b8e8e7f7c447efb450', 'ZAW_469b9890a6ed4ec2', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('546d117268b748e1a94e', '08b8e8e7f7c447efb450', 'ZAW_d2962d81f9e848a4', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('864f86c29c554ebdbe0c', '08b8e8e7f7c447efb450', 'ZAW_0d2b378287bd4d0d', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('9dabe935301f40aea65f', '08b8e8e7f7c447efb450', 'ZAW_a8ac6b6e58cb44e1', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('bc51538ac8c7419c81cb', '08b8e8e7f7c447efb450', 'ZAW_0a8212f3a34e40d1', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('d2358051680c4367817d', '6f2693fbfca742c2be3f', 'ZAW_e420e48865f04484', 'ZGLOSZONE', '2026-01-30 16:07:13'),
('dfe17cdbb65e41279e68', '08b8e8e7f7c447efb450', 'ZAW_e420e48865f04484', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('fb6631c8f28641a2828a', '08b8e8e7f7c447efb450', 'ZAW_5c3eeb6c15eb4416', 'ZGLOSZONE', '2026-01-30 00:03:35'),
('fd6111fe4f8d4d4baf89', '6f2693fbfca742c2be3f', 'ZAW_0a8212f3a34e40d1', 'ZGLOSZONE', '2026-01-30 16:09:06');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `administrator`
--
ALTER TABLE `administrator`
  ADD PRIMARY KEY (`id_administratora`),
  ADD UNIQUE KEY `id_konta` (`id_konta`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indeksy dla tabeli `kibic`
--
ALTER TABLE `kibic`
  ADD PRIMARY KEY (`id_kibica`),
  ADD UNIQUE KEY `id_konta` (`id_konta`),
  ADD UNIQUE KEY `pseudonim` (`pseudonim`);

--
-- Indeksy dla tabeli `konto`
--
ALTER TABLE `konto`
  ADD PRIMARY KEY (`id_konta`),
  ADD UNIQUE KEY `login` (`login`);

--
-- Indeksy dla tabeli `mecz`
--
ALTER TABLE `mecz`
  ADD PRIMARY KEY (`id_meczu`),
  ADD UNIQUE KEY `uq_round_slot` (`id_turnieju`,`runda`,`slot_w_rundzie`),
  ADD KEY `fk_m_a` (`id_zawodnik_a`),
  ADD KEY `fk_m_b` (`id_zawodnik_b`),
  ADD KEY `fk_m_w` (`id_zwyciezcy`),
  ADD KEY `fk_m_s` (`id_sedzia`);

--
-- Indeksy dla tabeli `organizator`
--
ALTER TABLE `organizator`
  ADD PRIMARY KEY (`id_organizatora`),
  ADD UNIQUE KEY `id_konta` (`id_konta`);

--
-- Indeksy dla tabeli `punkty_log`
--
ALTER TABLE `punkty_log`
  ADD PRIMARY KEY (`id_logu`),
  ADD UNIQUE KEY `uq_log_once` (`id_meczu`,`id_zawodnika`,`typ`),
  ADD KEY `idx_log_turniej` (`id_turnieju`),
  ADD KEY `idx_log_zawodnik` (`id_zawodnika`);

--
-- Indeksy dla tabeli `punkty_turniejowe`
--
ALTER TABLE `punkty_turniejowe`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_turniej_zawodnik` (`id_turnieju`,`id_zawodnika`);

--
-- Indeksy dla tabeli `sedzia`
--
ALTER TABLE `sedzia`
  ADD PRIMARY KEY (`id_sedzia`),
  ADD UNIQUE KEY `id_konta` (`id_konta`),
  ADD UNIQUE KEY `numer_licencji` (`numer_licencji`);

--
-- Indeksy dla tabeli `sedzia_turniej`
--
ALTER TABLE `sedzia_turniej`
  ADD PRIMARY KEY (`id_turnieju`,`id_sedzia`),
  ADD KEY `fk_st_sedzia` (`id_sedzia`);

--
-- Indeksy dla tabeli `turniej`
--
ALTER TABLE `turniej`
  ADD PRIMARY KEY (`id_turnieju`),
  ADD KEY `fk_turniej_organizator` (`id_organizatora`),
  ADD KEY `idx_turniej_status` (`status`),
  ADD KEY `idx_turniej_ranga` (`ranga`),
  ADD KEY `idx_turniej_sezon` (`sezon`);

--
-- Indeksy dla tabeli `zawodnik`
--
ALTER TABLE `zawodnik`
  ADD PRIMARY KEY (`id_zawodnika`),
  ADD UNIQUE KEY `id_konta` (`id_konta`),
  ADD KEY `idx_zawodnik_punkty` (`punkty`),
  ADD KEY `idx_zawodnik_kraj` (`kraj`);

--
-- Indeksy dla tabeli `zgloszenie`
--
ALTER TABLE `zgloszenie`
  ADD PRIMARY KEY (`id_zgloszenia`),
  ADD UNIQUE KEY `uq_zgloszenie_one` (`id_zawodnika`,`id_turnieju`),
  ADD KEY `idx_zgloszenie_turniej` (`id_turnieju`),
  ADD KEY `idx_zgloszenie_zawodnik` (`id_zawodnika`);

--
-- Indeksy dla tabeli `zgloszenie_turniejowe`
--
ALTER TABLE `zgloszenie_turniejowe`
  ADD PRIMARY KEY (`id_zgloszenia`),
  ADD UNIQUE KEY `uq_turniej_zawodnik` (`id_turnieju`,`id_zawodnika`),
  ADD KEY `fk_zgl_zawodnik` (`id_zawodnika`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `administrator`
--
ALTER TABLE `administrator`
  ADD CONSTRAINT `fk_admin_konto` FOREIGN KEY (`id_konta`) REFERENCES `konto` (`id_konta`) ON DELETE CASCADE;

--
-- Constraints for table `kibic`
--
ALTER TABLE `kibic`
  ADD CONSTRAINT `fk_kibic_konto` FOREIGN KEY (`id_konta`) REFERENCES `konto` (`id_konta`) ON DELETE CASCADE;

--
-- Constraints for table `mecz`
--
ALTER TABLE `mecz`
  ADD CONSTRAINT `fk_m_a` FOREIGN KEY (`id_zawodnik_a`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_m_b` FOREIGN KEY (`id_zawodnik_b`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_m_s` FOREIGN KEY (`id_sedzia`) REFERENCES `sedzia` (`id_sedzia`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_m_turniej` FOREIGN KEY (`id_turnieju`) REFERENCES `turniej` (`id_turnieju`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_m_w` FOREIGN KEY (`id_zwyciezcy`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `organizator`
--
ALTER TABLE `organizator`
  ADD CONSTRAINT `fk_org_konto` FOREIGN KEY (`id_konta`) REFERENCES `konto` (`id_konta`) ON DELETE CASCADE;

--
-- Constraints for table `punkty_log`
--
ALTER TABLE `punkty_log`
  ADD CONSTRAINT `fk_log_mecz` FOREIGN KEY (`id_meczu`) REFERENCES `mecz` (`id_meczu`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_log_turniej` FOREIGN KEY (`id_turnieju`) REFERENCES `turniej` (`id_turnieju`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_log_zawodnik` FOREIGN KEY (`id_zawodnika`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE CASCADE;

--
-- Constraints for table `sedzia`
--
ALTER TABLE `sedzia`
  ADD CONSTRAINT `fk_sedzia_konto` FOREIGN KEY (`id_konta`) REFERENCES `konto` (`id_konta`) ON DELETE CASCADE;

--
-- Constraints for table `sedzia_turniej`
--
ALTER TABLE `sedzia_turniej`
  ADD CONSTRAINT `fk_st_sedzia` FOREIGN KEY (`id_sedzia`) REFERENCES `sedzia` (`id_sedzia`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_st_turniej` FOREIGN KEY (`id_turnieju`) REFERENCES `turniej` (`id_turnieju`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `turniej`
--
ALTER TABLE `turniej`
  ADD CONSTRAINT `fk_turniej_organizator` FOREIGN KEY (`id_organizatora`) REFERENCES `organizator` (`id_organizatora`);

--
-- Constraints for table `zawodnik`
--
ALTER TABLE `zawodnik`
  ADD CONSTRAINT `fk_zawodnik_konto` FOREIGN KEY (`id_konta`) REFERENCES `konto` (`id_konta`) ON DELETE CASCADE;

--
-- Constraints for table `zgloszenie`
--
ALTER TABLE `zgloszenie`
  ADD CONSTRAINT `fk_zgloszenie_turniej` FOREIGN KEY (`id_turnieju`) REFERENCES `turniej` (`id_turnieju`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_zgloszenie_zawodnik` FOREIGN KEY (`id_zawodnika`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE CASCADE;

--
-- Constraints for table `zgloszenie_turniejowe`
--
ALTER TABLE `zgloszenie_turniejowe`
  ADD CONSTRAINT `fk_zgl_turniej` FOREIGN KEY (`id_turnieju`) REFERENCES `turniej` (`id_turnieju`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_zgl_zawodnik` FOREIGN KEY (`id_zawodnika`) REFERENCES `zawodnik` (`id_zawodnika`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4
-- Dumped by pg_dump version 15.4

-- Started on 2024-07-29 12:30:39

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3357 (class 0 OID 16688)
-- Dependencies: 219
-- Data for Name: debts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.debts (spending_id, user_id, amount) FROM stdin;
40	14	0
40	16	333
40	15	111
\.


--
-- TOC entry 3356 (class 0 OID 16610)
-- Dependencies: 218
-- Data for Name: friends; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friends (receiver_id, sender_id, date, status) FROM stdin;
16	14	2024-07-29	SENT
15	14	2024-07-29	ACCEPTED
\.

--
-- TOC entry 3359 (class 0 OID 16694)
-- Dependencies: 221
-- Data for Name: spendings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.spendings (id, creator_id, date, name, payer_id) FROM stdin;
40	14	2024-07-29	Счёт1	14
\.


--
-- TOC entry 3354 (class 0 OID 16568)
-- Dependencies: 216
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, name, password) FROM stdin;
14	user1	$2a$10$uHbGfb1P95mS0eauYYCOFuUOHZgJn6vOzl6qzx.kvwlcAnR8IehV6
15	user2	$2a$10$YWZ2mABZIokN8KnG5h3ike3k4bUoAlDga2wv1O3N1jWnYa50GF2si
16	user3	$2a$10$MOO1eBnb.XVIaOtyqU/qv.u1jVz9Cm6dxl7Byuuz4RyG6u.1oXc5y
17	user4	$2a$10$JwZRFZ0TB5JXB26SEoT0xOQpfMleS3APC9XtSNaHiDapBsV5qa4.K
18	user5	$2a$10$HWi06Z4aTYKM2GkO/eTksOgM1l2LFu1JbsAOaA0KxXGSpxNV2VLsK
\.


--
-- TOC entry 3355 (class 0 OID 16575)
-- Dependencies: 217
-- Data for Name: users_roles; Type: TABLE DATA; Schema: public; Owner: postgres


--
-- TOC entry 3365 (class 0 OID 0)
-- Dependencies: 220
-- Name: spendings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.spendings_id_seq', 40, true);


--
-- TOC entry 3366 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 20, true);


-- Completed on 2024-07-29 12:30:39

--
-- PostgreSQL database dump complete
--


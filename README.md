Food Market: projekt na Systemy inteligentnych agentów 2013
=============
Deadline
------------
13.06.2013
### HARD DEADLINE: 14.06.2013

Organizacja świata
-------
Agenci żyją w świecie, w którym co tydzień odbywa się targ, na którym mogą handlować towarami.
Czas jest globalny i każdy agent w każdym tygodniu uaktualnia stan swoich zasobów w zależności od przychodów i zapotrzebowań.

Komunikacja
-------

Cotygodniowa komunikacja między agentami przebiega w 8 etapach:

1. Każdy agent, który coś sprzedaje wysyła do kupujących informację o tym jakie towary, za jaką cenę i jak dużo chce/może sprzedać.
2. Agenci kupujący, otrzymują oferty od sprzedających.
3. Agenci po otrzymaniu ofert od wszystkich dostępnych sprzedających dokonują analizy w oparciu o własną strategię i wysyłają odpowiedzi do wszystkich sprzedających (*).
4. Sprzedający odbierają oferty klientów.
5. Po otrzymaniu ofert od wszystkich klientów (*) agenci sprzedający analizują oferty, ustalają ostatecznie cenę i ilość towaru, którą mogą sprzedać.
6. Kupujący akceptują albo odrzucają ofertę i wysyłają tę informację sprzedającemu.
7. Po otrzymaniu akceptacji oferty od klienta sprzedający uaktualnia stan magazynu i wysyła klientowi potwierdzenie transakcji (albo informuje go, że nie może ona zostać zrealizowana - jeśli w międzyczasie sprzedał towary komuś innemu).
8. Klient otrzymuje potwierdzenie albo rezygneację z transakcji od sprzedającego i odpowiednio modyfikuje swoje zapotrzebowania.

(*) być może nie jest to najlepsze rozwiązanie, bo zakłada ono, że trzeba też wysłać informację o tym, że nie jest się zainteresowanym transakcją

TODO
------------

* zaimplementować update resourców co tydzień
* opracować i zaimplementować kilka strategii agentów (które agenci będą mogli wybrać/wylosować i które raczej nie powinny się zmieniać)
* uruchomić na kilku konfiguracjach i wyciągnąć wnioski
* przygotować raport

farmer:
 -zaczyna z zasianym polem (czyli ma 0 nawozu i ziarna, ale ma ju� zasiane pole wi�c mo�e sprzeda� zbo�e i kupi� co potrzebuje)
 - sprzedaje przez ca�y rok
 
 chcemy mie� tygodniow� produkcj� a nie dzienn�, bo klienci kupuj� raz w tygodniu
 
 dla ka�dego agenta
 	protected EnumMap<Products, Integer> buy; - to co chce kupic i w jakiej ilosci
	protected EnumMap<Products, Integer> have; - to co ma BEZ tego co chce sprzedac (czyli to co ma na wlasny uzytek)
	protected EnumMap<Products, Integer> sell; - to co ma ale chce sprzedac
	
serviceDescription : nazwa sprzedawanego produktu
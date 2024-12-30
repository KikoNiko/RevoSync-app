const nav = document.querySelector('.nav');
  let lastScrollY = window.scrollY;

  window.addEventListener('scroll', () => {
    if (lastScrollY < window.scrollY) {
      nav.classList.add('nav--hidden');
    } else {
      nav.classList.remove('nav--hidden');
    }

    lastScrollY = window.scrollY;
  });


const menuBtn = document.querySelector('.burger-menu');
const menu = document.querySelector('.menu');

menuBtn.onclick = () => {
  menu.classList.toggle('open');
}
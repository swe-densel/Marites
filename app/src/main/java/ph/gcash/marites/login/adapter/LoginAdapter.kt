package ph.gcash.marites.login.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class LoginAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    override fun getCount() = fragmentList.size
    override fun getItem(position: Int) = fragmentList[position]
    fun add(fragment: Fragment) = fragmentList.add(fragment)
}